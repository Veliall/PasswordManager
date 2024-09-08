import dto.WebCredentialsDto
import kotlinx.coroutines.delay
import java.net.URI

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