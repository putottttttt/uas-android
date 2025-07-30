package com.sewagadget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val gadgetId: Int,
    val startDate: String, // Simpan sebagai String (contoh: "2024-07-28") atau Long (timestamp)
    val endDate: String,
    val totalPrice: Int,
    val status: String // "disewa" atau "selesai"
)