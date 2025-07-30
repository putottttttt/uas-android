package com.sewagadget.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts // Pastikan import ini ada
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.sewagadget.data.model.Category
import com.sewagadget.data.model.Gadget
import com.sewagadget.databinding.ActivityAddEditGadgetBinding
import com.sewagadget.ui.GadgetViewModel

class AddEditGadgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditGadgetBinding
    private val gadgetViewModel: GadgetViewModel by viewModels()

    private var categoryList = listOf<Category>()
    private var selectedCategoryId: Int? = null
    private var gadgetId: Int? = null
    private var imageUri: Uri? = null

    // --- PERUBAHAN DI SINI: Gunakan OpenDocument, bukan GetContent ---
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try {
                // Baris ini sekarang akan berhasil karena kita menggunakan OpenDocument
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageUri = it
                binding.ivGadgetPhoto.load(it)
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(this, "Gagal mengambil izin untuk gambar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditGadgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        gadgetId = intent.getIntExtra(EXTRA_GADGET_ID, -1).takeIf { it != -1 }

        setupCategorySpinner()
        setupListeners()

        if (gadgetId != null) {
            setupEditMode()
        } else {
            setupAddMode()
        }
    }

    private fun setupAddMode() {
        supportActionBar?.title = "Tambah Gadget Baru"
    }

    private fun setupEditMode() {
        supportActionBar?.title = "Edit Gadget"
        gadgetId?.let { id ->
            gadgetViewModel.getGadgetById(id).observe(this) { gadget ->
                gadget?.let { populateForm(it) }
            }
        }
    }

    private fun populateForm(gadget: Gadget) {
        binding.etGadgetName.setText(gadget.name)
        binding.etGadgetBrand.setText(gadget.brand)
        binding.etGadgetSpecs.setText(gadget.specs)
        binding.etGadgetPrice.setText(gadget.pricePerDay.toString())

        gadget.imageUri?.let {
            imageUri = Uri.parse(it)
            binding.ivGadgetPhoto.load(imageUri)
        }

        val categoryPosition = categoryList.indexOfFirst { it.id == gadget.categoryId }
        if (categoryPosition != -1) {
            binding.spinnerCategory.setSelection(categoryPosition)
        }
    }

    private fun setupCategorySpinner() {
        gadgetViewModel.allCategories.observe(this) { categories ->
            categoryList = categories
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }

        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (categoryList.isNotEmpty()) {
                    selectedCategoryId = categoryList[position].id
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategoryId = null
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveGadget()
        }
        binding.btnSelectPhoto.setOnClickListener {
            // --- PERUBAHAN DI SINI: Sesuaikan cara memanggil launch ---
            selectImageLauncher.launch(arrayOf("image/*"))
        }
    }

    private fun saveGadget() {
        val name = binding.etGadgetName.text.toString().trim()
        val brand = binding.etGadgetBrand.text.toString().trim()
        val specs = binding.etGadgetSpecs.text.toString().trim()
        val price = binding.etGadgetPrice.text.toString().toIntOrNull()
        val imageUriString = imageUri?.toString()

        if (name.isEmpty() || brand.isEmpty() || specs.isEmpty() || price == null || selectedCategoryId == null) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (gadgetId == null) {
            val newGadget = Gadget(name = name, brand = brand, specs = specs, pricePerDay = price, categoryId = selectedCategoryId!!, imageUri = imageUriString, isAvailable = true)
            gadgetViewModel.insert(newGadget)
            Toast.makeText(this, "Gadget berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        } else {
            // Saat update, kita perlu mengambil status isAvailable yang lama agar tidak ter-reset
            gadgetViewModel.getGadgetById(gadgetId!!).observe(this) { existingGadget ->
                if (existingGadget != null) {
                    val updatedGadget = Gadget(id = gadgetId!!, name = name, brand = brand, specs = specs, pricePerDay = price, categoryId = selectedCategoryId!!, imageUri = imageUriString, isAvailable = existingGadget.isAvailable)
                    gadgetViewModel.update(updatedGadget)
                }
            }
            Toast.makeText(this, "Gadget berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_GADGET_ID = "extra_gadget_id"
    }
}