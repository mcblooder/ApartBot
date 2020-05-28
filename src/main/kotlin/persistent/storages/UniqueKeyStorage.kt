package com.example.main.kotlin.persistent.storages

import com.example.main.kotlin.persistent.DB
import com.example.main.kotlin.persistent.models.UniqueKey
import com.example.main.kotlin.persistent.models.UniqueKeys
import org.jetbrains.exposed.sql.SchemaUtils

class UniqueKeyStorage(private val db: DB) {

    init {
        db.transaction {
            SchemaUtils.create(UniqueKeys)
        }
    }
    
    fun checkKeyExist(key: String): Boolean {
        return findUniqueKey(key) != null
    }

    fun findUniqueKey(key: String): UniqueKey? {
        return db.transaction {
            UniqueKey.find { UniqueKeys.key eq key }.firstOrNull()
        }
    }

    fun addUniqueKey(key: String) {
        if (checkKeyExist(key) == false) {
            db.transaction {
                UniqueKey.new {
                    this.key = key
                }
            }
        } else {
            println("Key ${key} already exist")
        }
    }
}