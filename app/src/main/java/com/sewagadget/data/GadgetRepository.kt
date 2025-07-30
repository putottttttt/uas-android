package com.sewagadget.data

import com.sewagadget.data.local.GadgetDao
import com.sewagadget.data.model.Gadget
import kotlinx.coroutines.flow.Flow

class GadgetRepository(private val gadgetDao: GadgetDao) {

    val allGadgets: Flow<List<Gadget>> = gadgetDao.getAllGadgets()

    fun getGadgetById(id: Int): Flow<Gadget?> {
        return gadgetDao.getGadgetById(id)
    }

    // --- FUNGSI BARU UNTUK FILTER ---
    fun getGadgetsByCategoryId(categoryId: Int): Flow<List<Gadget>> {
        return gadgetDao.getGadgetsByCategoryId(categoryId)
    }

    suspend fun insert(gadget: Gadget) {
        gadgetDao.insertGadget(gadget)
    }

    suspend fun update(gadget: Gadget) {
        gadgetDao.updateGadget(gadget)
    }

    suspend fun delete(gadget: Gadget) {
        gadgetDao.deleteGadget(gadget)
    }
}