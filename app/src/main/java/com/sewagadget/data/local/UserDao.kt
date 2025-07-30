package com.sewagadget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sewagadget.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findUserByUsername(username: String): User?

    // --- TAMBAHKAN FUNGSI INI ---
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun findUserById(userId: Int): User?
}