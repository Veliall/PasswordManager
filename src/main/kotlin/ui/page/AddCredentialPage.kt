package ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import database.dao.WebCredential
import database.dao.WebCredentialDao
import kotlinx.coroutines.launch

@Composable
fun AddCredentialPage(modifier: Modifier = Modifier, scaffoldState: ScaffoldState) {
    var uri by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val dao = remember { WebCredentialDao() }

    Column(modifier = modifier.padding(16.dp)) {
        TextField(
            value = uri,
            onValueChange = { uri = it },
            label = { Text("URI") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Login") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (uri.isBlank() || login.isBlank() || password.isBlank()) {
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "All fields must be filled!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        scope.launch {
                            // Сохранение новой записи в базе данных
                            val newCredential = WebCredential(uri = uri, login = login, password = password)
                            dao.insert(newCredential)

                            // Показываем сообщение о сохранении
                            scaffoldState.snackbarHostState.showSnackbar("Credential saved successfully")

                            // Очищаем поля
                            uri = ""
                            login = ""
                            password = ""
                        }
                    }
                }
            ) {
                Text("Save")
            }
        }
    }
}