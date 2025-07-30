package com.sewagadget.ui.history

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sewagadget.adapter.TransactionAdapter
import com.sewagadget.data.SessionManager
import com.sewagadget.databinding.ActivityHistoryBinding
import com.sewagadget.ui.transaction.TransactionViewModel

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private val transactionAdapter = TransactionAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- PERBAIKAN DI SINI ---
        // 1. Atur toolbar sebagai action bar
        setSupportActionBar(binding.toolbar)
        // 2. Sekarang kita bisa dengan aman mengubah action bar
        supportActionBar?.title = "Riwayat Penyewaan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sessionManager = SessionManager(this)
        binding.rvHistory.adapter = transactionAdapter

        observeHistory()
    }

    private fun observeHistory() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.getHistory(userId).observe(this) { historyList ->
                transactionAdapter.submitList(historyList)
            }
        } else {
            Toast.makeText(this, "Gagal memuat riwayat, silakan login kembali", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}