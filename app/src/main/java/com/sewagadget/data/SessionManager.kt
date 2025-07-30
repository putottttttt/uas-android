package com.sewagadget.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class untuk mengelola data sesi pengguna menggunakan SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "sewa_gadget_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Menyimpan sesi login pengguna.
     * @param userId ID dari pengguna yang login.
     */
    fun createLoginSession(userId: Int) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, userId)
        editor.apply()
    }

    /**
     * Mengambil ID pengguna yang sedang login.
     * @return ID pengguna, atau -1 jika tidak ada yang login.
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    /**
     * Menghapus data sesi saat logout.
     */
    fun logoutUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}