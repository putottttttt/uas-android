package com.sewagadget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction as RoomTransaction // Alias untuk menghindari konflik nama
import com.sewagadget.data.model.Transaction
import com.sewagadget.data.model.TransactionWithGadget
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Deprecated("Gunakan getTransactionDetailsForUser untuk data yang lebih lengkap")
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY id DESC")
    fun getTransactionsForUser(userId: Int): Flow<List<Transaction>>

    // --- TAMBAHKAN FUNGSI INI ---
    @RoomTransaction
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY id DESC")
    fun getTransactionDetailsForUser(userId: Int): Flow<List<TransactionWithGadget>>
}