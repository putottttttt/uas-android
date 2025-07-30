package com.sewagadget.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sewagadget.data.SessionManager
import com.sewagadget.data.local.AppDatabase
import com.sewagadget.data.model.User
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val sessionManager = SessionManager(application)

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _errorMessage.value = "Username dan password tidak boleh kosong!"
            return
        }

        viewModelScope.launch {
            val user = userDao.findUserByUsername(username)
            if (user != null && user.password == password) {
                _loginResult.postValue(user)
            } else {
                _loginResult.postValue(null) // Reset jika gagal
                _errorMessage.postValue("Username atau password salah.")
            }
        }
    }

    // --- TAMBAHKAN FUNGSI INI ---
    /**
     * Memeriksa apakah ada sesi aktif. Jika ada, coba login otomatis.
     */
    fun checkSession() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModelScope.launch {
                val user = userDao.findUserById(userId)
                // Langsung post ke loginResult, observer di Activity akan menangani navigasi
                _loginResult.postValue(user)
            }
        }
    }
}