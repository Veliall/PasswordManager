package database.dao

import database.table.WebCredentialsTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

data class WebCredential(
    val id: Int = 0,
    val uri: String,
    val login: String,
    val password: String
)

class CredentialDao {
    suspend fun insert(webCredential: WebCredential) {
        newSuspendedTransaction(Dispatchers.IO) {
            WebCredentialsTable.insert {
                it[uri] = webCredential.uri
                it[login] = webCredential.login
                it[password] = webCredential.password
            }
        }
    }

    suspend fun update(webCredential: WebCredential) {
        newSuspendedTransaction(Dispatchers.IO) {
            WebCredentialsTable.update({ WebCredentialsTable.id eq webCredential.id }) {
                it[uri] = webCredential.uri
                it[login] = webCredential.login
                it[password] = webCredential.password
            }
        }
    }

    suspend fun delete(id: Int) {
        newSuspendedTransaction(Dispatchers.IO) {
            WebCredentialsTable.deleteWhere { WebCredentialsTable.id eq id }
        }
    }

    suspend fun getAllInPage(limit: Int, offset: Long): List<WebCredential> {
        return newSuspendedTransaction(Dispatchers.IO) {
            WebCredentialsTable.selectAll()
                .limit(limit, offset)
                .map {
                    WebCredential(
                        id = it[WebCredentialsTable.id],
                        uri = it[WebCredentialsTable.uri],
                        login = it[WebCredentialsTable.login],
                        password = it[WebCredentialsTable.password]
                    )
                }

        }
    }
}