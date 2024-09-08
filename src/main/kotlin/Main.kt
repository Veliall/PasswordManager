import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import database.DatabaseFactory
import ui.PaginatedCredentialsList

fun main() = application {
    DatabaseFactory.init()
    Window(
        title = "Password Manager",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        PaginatedCredentialsList()
    }
}
