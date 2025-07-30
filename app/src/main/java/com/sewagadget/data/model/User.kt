package com.sewagadget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String, // Dalam aplikasi nyata, password harus di-hash
    val role: String // "admin" atau "customer"
)