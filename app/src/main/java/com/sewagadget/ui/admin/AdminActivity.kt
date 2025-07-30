package com.sewagadget.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sewagadget.R
import com.sewagadget.adapter.GadgetAdapter
import com.sewagadget.data.SessionManager
import com.sewagadget.data.model.Gadget
import com.sewagadget.databinding.ActivityAdminBinding
import com.sewagadget.ui.GadgetViewModel
import com.sewagadget.ui.auth.LoginActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val gadgetViewModel: GadgetViewModel by viewModels()
    private lateinit var gadgetAdapter: GadgetAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur toolbar kustom sebagai action bar utama untuk activity ini
        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Admin Dashboard"
        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Atur filter awal untuk menampilkan semua gadget
        gadgetViewModel.setFilter(-1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Menampilkan menu (termasuk tombol logout) di toolbar
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                handleLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleLogout() {
        sessionManager.logoutUser() // Hapus sesi
        val intent = Intent(this, LoginActivity::class.java)
        // Hapus semua activity sebelumnya dari back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupRecyclerView() {
        gadgetAdapter = GadgetAdapter(
            userRole = "admin",
            onItemClick = { gadget ->
                Toast.makeText(this, "Anda memilih ${gadget.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { gadget ->
                handleEditAction(gadget)
            },
            onDeleteClick = { gadget ->
                handleDeleteAction(gadget)
            }
        )
        binding.rvGadgets.apply {
            adapter = gadgetAdapter
            layoutManager = LinearLayoutManager(this@AdminActivity)
        }
    }

    private fun setupObservers() {
        gadgetViewModel.filteredGadgets.observe(this) { gadgets ->
            gadgets?.let { gadgetAdapter.submitList(it) }
        }
    }

    private fun setupListeners() {
        binding.fabAddGadget.setOnClickListener {
            val intent = Intent(this, AddEditGadgetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleEditAction(gadget: Gadget) {
        val intent = Intent(this, AddEditGadgetActivity::class.java)
        intent.putExtra(AddEditGadgetActivity.EXTRA_GADGET_ID, gadget.id)
        startActivity(intent)
    }

    private fun handleDeleteAction(gadget: Gadget) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Gadget")
            .setMessage("Apakah Anda yakin ingin menghapus '${gadget.name}'?")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                gadgetViewModel.delete(gadget)
                Toast.makeText(this, "'${gadget.name}' telah dihapus.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}