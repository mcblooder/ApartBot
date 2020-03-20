package com.example.invokables.apartments.storages

import com.example.persistent.DB
import com.example.persistent.models.UniqueKey
import com.example.persistent.models.UniqueKeys
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