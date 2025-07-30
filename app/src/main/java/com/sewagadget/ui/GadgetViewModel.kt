package com.sewagadget.ui

import android.app.Application
import androidx.lifecycle.*
import com.sewagadget.data.GadgetRepository
import com.sewagadget.data.local.AppDatabase
import com.sewagadget.data.local.CategoryDao
import com.sewagadget.data.model.Category
import com.sewagadget.data.model.Gadget
import kotlinx.coroutines.launch

class GadgetViewModel(application: Application) : AndroidViewModel(application) {

    // --- PERUBAHAN 1: Deklarasikan dengan lateinit var ---
    private var repository: GadgetRepository
    private var categoryDao: CategoryDao
    var allCategories: LiveData<List<Category>>
    var filteredGadgets: LiveData<List<Gadget>>

    private val _activeFilterCategoryId = MutableLiveData<Int>(-1)

    // --- PERUBAHAN 2: Inisialisasi semua di dalam blok init ---
    init {
        val database = AppDatabase.getDatabase(application)

        // Inisialisasi DAO dan Repository terlebih dahulu
        categoryDao = database.categoryDao()
        repository = GadgetRepository(database.gadgetDao())

        // Setelah repository siap, baru inisialisasi LiveData yang bergantung padanya
        allCategories = categoryDao.getAllCategories().asLiveData()
        filteredGadgets = _activeFilterCategoryId.switchMap { categoryId ->
            if (categoryId == -1) {
                repository.allGadgets.asLiveData()
            } else {
                repository.getGadgetsByCategoryId(categoryId).asLiveData()
            }
        }
    }

    fun getGadgetById(id: Int): LiveData<Gadget?> {
        return repository.getGadgetById(id).asLiveData()
    }

    fun setFilter(categoryId: Int) {
        _activeFilterCategoryId.value = categoryId
    }

    fun insert(gadget: Gadget) = viewModelScope.launch {
        repository.insert(gadget)
    }

    fun update(gadget: Gadget) = viewModelScope.launch {
        repository.update(gadget)
    }

    fun delete(gadget: Gadget) = viewModelScope.launch {
        repository.delete(gadget)
    }
}