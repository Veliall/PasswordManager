import androidx.compose.desktop.ui.tooling.preview.Preview
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URI

@Composable
@Preview
fun App() {
    MaterialTheme {
        PaginatedCredentialsList(
            itemsPerPage = 10,
            fetchData = { page ->
                fetchCredentials(page, itemsPerPage = 10)
            }
        )
    }
}


@Composable
fun PaginatedCredentialsList(
    itemsPerPage: Int,
    fetchData: suspend (Int) -> List<WebCredentialsDto>
) {
    // Состояние текущей страницы
    var currentPage by remember { mutableStateOf(0) }
    var items by remember { mutableStateOf<List<WebCredentialsDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Функция для загрузки данных текущей страницы
    fun loadPage(page: Int) {
        scope.launch {
            isLoading = true
            items = fetchData(page)
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

// Симуляция функции для загрузки данных
suspend fun fetchCredentials(page: Int, itemsPerPage: Int): List<WebCredentialsDto> {
    delay(1000) // Имитация задержки сети или базы данных
    val start = page * itemsPerPage + 1
    return (start until start + itemsPerPage).map {
        WebCredentialsDto(
            URI("https://example.com/$it"),
            login = "user$it",
            password = "password$it"
        )
    }
}
