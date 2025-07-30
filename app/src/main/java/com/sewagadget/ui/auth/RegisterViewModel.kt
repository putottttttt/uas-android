package com.sewagadget.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sewagadget.data.local.AppDatabase
import com.sewagadget.data.model.User
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    // LiveData untuk memberi tahu UI tentang status registrasi
    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

    fun registerUser(username: String, password_one: String, password_two: String) {
        // Validasi input
        if (username.isBlank() || password_one.isBlank() || password_two.isBlank()) {
            _registrationStatus.value = RegistrationStatus.Error("Semua kolom harus diisi.")
            return
        }

        if (password_one != password_two) {
            _registrationStatus.value = RegistrationStatus.Error("Password tidak cocok.")
            return
        }

        viewModelScope.launch {
            // Cek apakah username sudah ada
            val existingUser = userDao.findUserByUsername(username)
            if (existingUser != null) {
                _registrationStatus.postValue(RegistrationStatus.Error("Username sudah digunakan."))
            } else {
                // Buat user baru dengan peran "customer"
                val newUser = User(username = username, password = password_one, role = "customer")
                userDao.registerUser(newUser)
                _registrationStatus.postValue(RegistrationStatus.Success)
            }
        }
    }
}

// Sealed class untuk merepresentasikan status registrasi dengan lebih baik
sealed class RegistrationStatus {
    data object Success : RegistrationStatus()
    data class Error(val message: String) : RegistrationStatus()
}