package com.example.main.kotlin.persistent.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UniqueKeys: IntIdTable() {
    val key = varchar("key", 50)
}

class UniqueKey(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UniqueKey>(UniqueKeys)

    var key by UniqueKeys.key
}