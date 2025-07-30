package com.sewagadget.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.sewagadget.R
import com.sewagadget.adapter.GadgetAdapter
import com.sewagadget.data.SessionManager
import com.sewagadget.data.model.Category
import com.sewagadget.databinding.ActivityCustomerBinding
import com.sewagadget.ui.GadgetViewModel
import com.sewagadget.ui.auth.LoginActivity
import com.sewagadget.ui.history.HistoryActivity
import com.sewagadget.ui.rental.RentalActivity

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerBinding
    private val gadgetViewModel: GadgetViewModel by viewModels()
    private lateinit var gadgetAdapter: GadgetAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur toolbar kustom sebagai action bar utama untuk activity ini
        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Pilih Gadget"
        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Menampilkan menu (Riwayat dan Logout) di toolbar
        menuInflater.inflate(R.menu.customer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                handleLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleLogout() {
        sessionManager.logoutUser()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupRecyclerView() {
        gadgetAdapter = GadgetAdapter(
            userRole = "customer",
            onItemClick = { selectedGadget ->
                val intent = Intent(this, RentalActivity::class.java)
                intent.putExtra(RentalActivity.EXTRA_GADGET_ID, selectedGadget.id)
                startActivity(intent)
            },
            onEditClick = {},
            onDeleteClick = {}
        )
        binding.rvGadgetsCustomer.apply {
            adapter = gadgetAdapter
            layoutManager = LinearLayoutManager(this@CustomerActivity)
        }
    }

    private fun setupObservers() {
        gadgetViewModel.allCategories.observe(this) { categories ->
            if (categories != null && categories.isNotEmpty()) {
                setupCategoryChips(categories)
            }
        }

        gadgetViewModel.filteredGadgets.observe(this) { gadgets ->
            gadgets?.let {
                val availableGadgets = it.filter { gadget -> gadget.isAvailable }
                gadgetAdapter.submitList(availableGadgets)
            }
        }
    }

    private fun setupCategoryChips(categories: List<Category>) {
        binding.chipGroupFilter.removeAllViews()

        val allChip = createChip("Semua", -1)
        binding.chipGroupFilter.addView(allChip)
        allChip.isChecked = true

        categories.forEach { category ->
            binding.chipGroupFilter.addView(createChip(category.name, category.id))
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChip = group.findViewById<Chip>(checkedIds.first())
                val categoryId = selectedChip.tag as Int
                gadgetViewModel.setFilter(categoryId)
            } else {
                allChip.isChecked = true
            }
        }
    }

    private fun createChip(name: String, id: Int): Chip {
        return Chip(this).apply {
            text = name
            tag = id
            isCheckable = true
            isClickable = true
            isFocusable = true
        }
    }
}