package com.sewagadget.ui.transaction

import android.app.Application
import androidx.lifecycle.*
import com.sewagadget.data.TransactionRepository
import com.sewagadget.data.local.AppDatabase
import com.sewagadget.data.model.Transaction
import com.sewagadget.data.model.TransactionWithGadget
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application): AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
    }

    /**
     * Menyimpan transaksi baru ke database.
     */
    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }

    /**
     * Mengambil riwayat transaksi untuk seorang pengguna.
     * @param userId ID dari pengguna yang riwayatnya ingin ditampilkan.
     */
    fun getHistory(userId: Int): LiveData<List<TransactionWithGadget>> {
        return repository.getTransactionDetailsForUser(userId).asLiveData()
    }
}