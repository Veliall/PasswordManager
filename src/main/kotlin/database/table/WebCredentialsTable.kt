package database.table

import org.jetbrains.exposed.sql.Table

object WebCredentialsTable : Table("web_credentials") {
    val id = integer("id").autoIncrement()
    val uri = varchar("uri", length = 255)
    val login = varchar("login", length = 255)
    val password = varchar("password", length = 255)
    override val primaryKey = PrimaryKey(id)
}