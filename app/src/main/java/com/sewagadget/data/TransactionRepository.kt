package com.sewagadget.data

import com.sewagadget.data.local.TransactionDao
import com.sewagadget.data.model.Transaction
import com.sewagadget.data.model.TransactionWithGadget
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    fun getTransactionDetailsForUser(userId: Int): Flow<List<TransactionWithGadget>> {
        return transactionDao.getTransactionDetailsForUser(userId)
    }
}