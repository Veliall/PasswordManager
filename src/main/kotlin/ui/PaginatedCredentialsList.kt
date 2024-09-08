package ui

import dto.WebCredentialsDto
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
import androidx.compose.ui.unit.dp
import fetchCredentials
import kotlinx.coroutines.launch


@Composable
fun PaginatedCredentialsList() {
    // Состояние текущей страницы
    var currentPage by remember { mutableStateOf(0) }
    var items by remember { mutableStateOf<List<WebCredentialsDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Функция для загрузки данных текущей страницы
    fun loadPage(page: Int) {
        scope.launch {
            isLoading = true
            items = fetchCredentials(page, itemsPerPage = 10)
            isLoading = false
        }
    }

    // Загрузка первой страницы при инициализации
    LaunchedEffect(currentPage) {
        loadPage(currentPage)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Отображаем текущую страницу карточек или индикатор загрузки
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 256.dp)
            ) {
                items(
                    count = items.size,
                    key = { index -> items[index].site },
                    itemContent = { index ->
                        val credential = items[index]
                        CredentialCard(credential)
                    }
                )
            }
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

