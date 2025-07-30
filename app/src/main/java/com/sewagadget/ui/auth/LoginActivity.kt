package com.sewagadget.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sewagadget.data.SessionManager
import com.sewagadget.databinding.ActivityLoginBinding
import com.sewagadget.ui.admin.AdminActivity
import com.sewagadget.ui.customer.CustomerActivity

/**
 * LoginActivity adalah layar pertama yang dilihat pengguna.
 *
 * Fitur:
 * - Menangani input login dari pengguna.
 * - Menavigasikan ke halaman Registrasi.
 * - Memeriksa sesi: jika pengguna sudah login, langsung arahkan ke activity yang sesuai.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Panggil observer sebelum checkSession agar siap menerima data
        setupObservers()

        // Periksa sesi pengguna saat activity dibuat
        loginViewModel.checkSession()

        setupListeners()
    }

    /**
     * Mengatur semua listener untuk komponen UI interaktif.
     */
    private fun setupListeners() {
        // Listener untuk tombol Login
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            loginViewModel.login(username, password)
        }

        // Listener untuk teks "Registrasi"
        binding.tvRegisterPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Mengamati LiveData dari ViewModel untuk menangani hasil login dan navigasi.
     */
    private fun setupObservers() {
        // Observer untuk hasil login (baik dari form maupun dari sesi)
        loginViewModel.loginResult.observe(this) { user ->
            user?.let {
                // Simpan sesi jika login berhasil
                sessionManager.createLoginSession(it.id)

                Toast.makeText(this, "Login berhasil! Role: ${it.role}", Toast.LENGTH_SHORT).show()

                // Arahkan ke activity yang sesuai berdasarkan peran
                if (it.role == "admin") {
                    startActivity(Intent(this, AdminActivity::class.java))
                } else {
                    startActivity(Intent(this, CustomerActivity::class.java))
                }

                // Tutup LoginActivity agar tidak bisa kembali dengan tombol back
                finish()
            }
        }

        // Observer untuk pesan error
        loginViewModel.errorMessage.observe(this) { message ->
            // Pastikan pesan tidak kosong sebelum menampilkannya
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}