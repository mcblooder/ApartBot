package com.example.persistent

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class DB {

    private val db = Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            addLogger(StdOutSqlLogger)
        }
    }

    fun <T> transaction(statement: Transaction.() -> T): T {
        return transaction(db) { statement() }
    }
}