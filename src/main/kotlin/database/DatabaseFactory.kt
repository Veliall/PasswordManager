package database

import database.table.WebCredentialsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {
    fun init() {
        Database.connect("jdbc:sqlite:credentials.db", driver = "org.sqlite.JDBC")
        transaction {
            create(WebCredentialsTable)
        }
    }
}