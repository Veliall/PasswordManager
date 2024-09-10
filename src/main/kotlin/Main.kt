import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import database.DatabaseFactory
import ui.DropdownMenuButton
import ui.page.AddCredentialPage
import ui.page.CredentialsListPage

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
    val scaffoldState = rememberScaffoldState()
    var currentPage by remember { mutableStateOf("list") } // Управляем текущей страницей

    MaterialTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("Password Manager") },
                    actions = {
                        DropdownMenuButton { selectedPage ->
                            currentPage = selectedPage
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) }
        ) { paddingValues ->
            when (currentPage) {
                "list" -> CredentialsListPage(modifier = Modifier.padding(paddingValues))
                "add" -> AddCredentialPage(modifier = Modifier.padding(paddingValues),
                    scaffoldState = scaffoldState)
            }
        }
    }
}
