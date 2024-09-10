package database.dao

import database.table.WebCredentialsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

data class WebCredential(
    val id: Int = 0,
    val uri: String,
    val login: String,
    val password: String
)

class WebCredentialDao : Dao {
    suspend fun insert(webCredential: WebCredential) = dbQuery {
        WebCredentialsTable.insert {
            it[uri] = webCredential.uri
            it[login] = webCredential.login
            it[password] = webCredential.password
        }
    }

    suspend fun update(webCredential: WebCredential) = dbQuery {
        WebCredentialsTable.update({ WebCredentialsTable.id eq webCredential.id }) {
            it[uri] = webCredential.uri
            it[login] = webCredential.login
            it[password] = webCredential.password

        }
    }

    suspend fun delete(id: Int) = dbQuery {
        WebCredentialsTable.deleteWhere { WebCredentialsTable.id eq id }

    }

    suspend fun getAllInPage(limit: Int, offset: Long): List<WebCredential> = dbQuery {
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