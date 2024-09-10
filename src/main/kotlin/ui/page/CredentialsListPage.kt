package ui.page

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import database.dao.WebCredential
import database.dao.WebCredentialDao
import kotlinx.coroutines.launch
import ui.CredentialCard


@Composable
fun CredentialsListPage(modifier: Modifier = Modifier) {
    var currentPage by remember { mutableStateOf(0) }
    var items by remember { mutableStateOf<List<WebCredential>>(emptyList()) }
    var filteredItems by remember { mutableStateOf<List<WebCredential>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val itemsOnPage = 10
    val dao = remember { WebCredentialDao() }
    val scope = rememberCoroutineScope()

    fun loadPage(page: Int) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                items = dao.getAllInPage(itemsOnPage, page * itemsOnPage.toLong())
                filteredItems = items
            } catch (e: Exception) {
                errorMessage = "Error loading data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(currentPage) {
        loadPage(currentPage)
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 256.dp),
                modifier = Modifier.weight(1f)
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
