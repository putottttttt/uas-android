package com.sewagadget.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sewagadget.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- PERBAIKAN FINAL: HAPUS KODE YANG MENGAKSES ACTION BAR ---
        // Menghapus baris di bawah ini akan mencegah crash karena supportActionBar null.
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // supportActionBar?.title = "Registrasi"

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val passOne = binding.etPasswordOne.text.toString()
            val passTwo = binding.etPasswordTwo.text.toString()
            registerViewModel.registerUser(username, passOne, passTwo)
        }
    }

    private fun setupObservers() {
        registerViewModel.registrationStatus.observe(this) { status ->
            when (status) {
                is RegistrationStatus.Success -> {
                    Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                    finish() // Kembali ke halaman Login
                }
                is RegistrationStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fungsi onSupportNavigateUp() tidak lagi diperlukan jika Anda menghapus
    // supportActionBar?.setDisplayHomeAsUpEnabled(true), namun bisa tetap ada
    // tanpa menyebabkan masalah.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}