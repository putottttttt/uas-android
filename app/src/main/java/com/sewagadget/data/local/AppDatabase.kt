package com.sewagadget.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sewagadget.data.model.Category
import com.sewagadget.data.model.Gadget
import com.sewagadget.data.model.Transaction
import com.sewagadget.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Database(
    entities = [User::class, Category::class, Gadget::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun gadgetDao(): GadgetDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sewa_gadget_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                // Menggunakan Coroutine untuk mengisi data di background thread
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.categoryDao(), database.gadgetDao(), database.userDao())
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao, gadgetDao: GadgetDao, userDao: UserDao) {
            // Hapus data lama jika ada (opsional)
            // gadgetDao.deleteAll()
            // categoryDao.deleteAll()

            // Tambah user admin default
            userDao.registerUser(User(username = "admin", password = "admin", role = "admin"))
            userDao.registerUser(User(username = "customer", password = "customer", role = "customer"))

            // Tambah kategori
            val categories = listOf(
                Category(id = 1, name = "HP"),
                Category(id = 2, name = "Laptop"),
                Category(id = 3, name = "Kamera")
            )
            categoryDao.insertAll(categories)

            // Tambah beberapa gadget
            val gadgets = listOf(
                Gadget(name = "iPhone 14 Pro", brand = "Apple", specs = "A16 Bionic, 256GB", pricePerDay = 150000, categoryId = 1),
                Gadget(name = "Galaxy S23 Ultra", brand = "Samsung", specs = "Snapdragon 8 Gen 2, 256GB", pricePerDay = 140000, categoryId = 1),
                Gadget(name = "MacBook Air M2", brand = "Apple", specs = "8-core CPU, 8-core GPU, 8GB RAM", pricePerDay = 200000, categoryId = 2),
                Gadget(name = "ThinkPad X1 Carbon", brand = "Lenovo", specs = "Core i7, 16GB RAM, 512GB SSD", pricePerDay = 220000, categoryId = 2),
                Gadget(name = "Alpha 7 IV", brand = "Sony", specs = "33MP Full-Frame, 4K 60p", pricePerDay = 300000, categoryId = 3),
                Gadget(name = "EOS R6 Mark II", brand = "Canon", specs = "24.2MP Full-Frame, 4K 60p", pricePerDay = 320000, categoryId = 3)
            )
            gadgetDao.insertAll(gadgets)
        }
    }
}