package com.sewagadget.data.local

import androidx.room.*
import com.sewagadget.data.model.Gadget
import kotlinx.coroutines.flow.Flow

@Dao
interface GadgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGadget(gadget: Gadget)

    @Update
    suspend fun updateGadget(gadget: Gadget)

    @Delete
    suspend fun deleteGadget(gadget: Gadget)

    @Query("SELECT * FROM gadgets ORDER BY name ASC")
    fun getAllGadgets(): Flow<List<Gadget>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(gadgets: List<Gadget>)

    @Query("SELECT * FROM gadgets WHERE id = :gadgetId")
    fun getGadgetById(gadgetId: Int): Flow<Gadget?>

    // --- FUNGSI BARU UNTUK FILTER ---
    /**
     * Mengambil semua gadget dari kategori tertentu.
     * @param categoryId ID dari kategori yang dicari.
     */
    @Query("SELECT * FROM gadgets WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getGadgetsByCategoryId(categoryId: Int): Flow<List<Gadget>>
}