package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import database.dao.CredentialDao
import database.dao.WebCredential
import kotlinx.coroutines.launch


@Composable
fun PaginatedCredentialsList() {
    var currentPage by remember { mutableStateOf(0) }
    var items by remember { mutableStateOf<List<WebCredential>>(emptyList()) }
    var filteredItems by remember { mutableStateOf<List<WebCredential>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) } // Состояние для показа диалога
    var searchQuery by remember { mutableStateOf("") } // Состояние для строки поиска

    val itemsOnPage = 10
    val dao = remember { CredentialDao() }
    val scope = rememberCoroutineScope()

    // Функция для загрузки данных текущей страницы
    fun loadPage(page: Int) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                items = dao.getAllInPage(itemsOnPage, page * itemsOnPage.toLong())
                filteredItems = items // Инициализация фильтрованного списка
            } catch (e: Exception) {
                errorMessage = "Error loading data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Функция для фильтрации записей по запросу
    fun filterItems(query: String) {
        // todo change to query in bd with filter
        filteredItems = if (query.isBlank()) {
            items
        } else {
            items.filter { it.uri.contains(query, ignoreCase = true) || it.login.contains(query, ignoreCase = true) }
        }
    }

    LaunchedEffect(currentPage) {
        loadPage(currentPage)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        topBar = {
            TopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    searchQuery = it
                    filterItems(it) // Фильтрация при каждом изменении строки поиска
                },
                onAddButtonClick = { showAddDialog = true }
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                // Отображаем текущую страницу карточек или индикатор загрузки
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 256.dp),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        items(
                            count = filteredItems.size,
                            key = { index -> filteredItems[index].id },
                            itemContent = { index ->
                                val credential = filteredItems[index]
                                CredentialCard(credential)
                            }
                        )
                    }
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Кнопки для переключения страниц
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (currentPage > 0) currentPage-- },
                        enabled = currentPage > 0
                    ) {
                        Text("Previous")
                    }

                    Text(
                        text = "Page ${currentPage + 1}",
                        style = MaterialTheme.typography.subtitle1
                    )

                    Button(
                        onClick = { currentPage++ },
                        enabled = !isLoading
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    )

    // Диалоговое окно для добавления новой записи
    if (showAddDialog) {
        AddCredentialDialog(
            onDismiss = { showAddDialog = false },
            onSave = { credential ->
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    try {
                        dao.insert(credential)
                        loadPage(currentPage) // Перезагружаем текущую страницу
                    } catch (e: Exception) {
                        errorMessage = "Error adding credential: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        )
    }
}


