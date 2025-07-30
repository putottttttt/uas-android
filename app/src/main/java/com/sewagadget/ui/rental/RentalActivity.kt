package com.sewagadget.ui.rental

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sewagadget.data.SessionManager
import com.sewagadget.data.model.Gadget
import com.sewagadget.data.model.Transaction
import com.sewagadget.databinding.ActivityRentalBinding
import com.sewagadget.ui.GadgetViewModel
import com.sewagadget.ui.auth.LoginActivity
import com.sewagadget.ui.transaction.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * RentalActivity adalah layar tempat pengguna (customer) melakukan proses penyewaan.
 *
 * Fitur:
 * - Menerima ID gadget dari activity sebelumnya.
 * - Menampilkan detail gadget yang akan disewa.
 * - Menyediakan DatePickerDialog untuk memilih tanggal mulai dan selesai.
 * - Menghitung total biaya sewa secara otomatis.
 * - Menyimpan data transaksi dan memperbarui status ketersediaan gadget.
 */
class RentalActivity : AppCompatActivity() {

    // Properti untuk View Binding, ViewModel, dan SessionManager
    private lateinit var binding: ActivityRentalBinding
    private val gadgetViewModel: GadgetViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    // Properti untuk menyimpan data sementara
    private var gadgetId: Int = -1
    private var currentGadget: Gadget? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Konfigurasi ActionBar
        supportActionBar?.title = "Formulir Penyewaan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inisialisasi SessionManager dan ambil ID gadget dari Intent
        sessionManager = SessionManager(this)
        gadgetId = intent.getIntExtra(EXTRA_GADGET_ID, -1)

        // Validasi awal: jika tidak ada ID gadget, tutup activity
        if (gadgetId == -1) {
            Toast.makeText(this, "Gadget tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Panggil fungsi helper
        observeGadget()
        setupListeners()
    }

    /**
     * Mengamati data gadget dari ViewModel berdasarkan ID.
     * Jika data ditemukan, perbarui UI dengan detail gadget.
     */
    @SuppressLint("SetTextI18n")
    private fun observeGadget() {
        gadgetViewModel.getGadgetById(gadgetId).observe(this) { gadget ->
            if (gadget != null) {
                currentGadget = gadget
                binding.tvGadgetName.text = gadget.name
                val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                binding.tvGadgetPrice.text = "${formatter.format(gadget.pricePerDay)} / hari"
            }
        }
    }

    /**
     * Mengatur semua listener untuk komponen UI interaktif.
     */
    private fun setupListeners() {
        binding.tvStartDate.setOnClickListener { showDatePicker(isStartDate = true) }
        binding.tvEndDate.setOnClickListener { showDatePicker(isStartDate = false) }
        binding.btnRentNow.setOnClickListener { processRental() }
    }

    /**
     * Menampilkan dialog pemilih tanggal (DatePickerDialog).
     * @param isStartDate Flag untuk membedakan apakah yang dipilih adalah tanggal mulai atau selesai.
     */
    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))

                if (isStartDate) {
                    startDate = selectedDate
                    binding.tvStartDate.text = sdf.format(selectedDate.time)
                } else {
                    endDate = selectedDate
                    binding.tvEndDate.text = sdf.format(selectedDate.time)
                }
                // Hitung ulang total biaya setiap kali tanggal berubah
                calculateTotalPrice()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Mencegah pengguna memilih tanggal di masa lalu.
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    /**
     * Menghitung dan menampilkan total biaya sewa berdasarkan durasi.
     */
    private fun calculateTotalPrice() {
        if (startDate != null && endDate != null && currentGadget != null) {
            // Validasi: tanggal selesai tidak boleh sebelum tanggal mulai
            if (endDate!!.before(startDate)) {
                Toast.makeText(this, "Tanggal selesai tidak boleh sebelum tanggal mulai", Toast.LENGTH_SHORT).show()
                binding.btnRentNow.isEnabled = false
                return
            }

            // Hitung durasi dalam hari
            val diffInMillis = endDate!!.timeInMillis - startDate!!.timeInMillis
            // Ditambah 1 karena hari pertama dihitung sebagai 1 hari sewa
            val durationInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1

            val totalPrice = durationInDays * currentGadget!!.pricePerDay

            // Format harga ke Rupiah dan tampilkan
            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvTotalPrice.text = formatter.format(totalPrice)
            binding.btnRentNow.isEnabled = true
        }
    }

    /**
     * Memproses transaksi penyewaan saat tombol "Sewa Sekarang" diklik.
     */
    private fun processRental() {
        // Validasi sesi pengguna
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            Toast.makeText(this, "Sesi Anda telah berakhir, silakan login kembali.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        // Buat objek transaksi baru dari data yang ada
        val newTransaction = Transaction(
            userId = userId,
            gadgetId = gadgetId,
            startDate = binding.tvStartDate.text.toString(),
            endDate = binding.tvEndDate.text.toString(),
            totalPrice = binding.tvTotalPrice.text.toString().filter { it.isDigit() }.toInt(),
            status = "disewa"
        )

        // Panggil ViewModel untuk menyimpan transaksi
        transactionViewModel.insertTransaction(newTransaction)

        // Panggil ViewModel untuk memperbarui status gadget menjadi tidak tersedia
        currentGadget?.let {
            it.isAvailable = false
            gadgetViewModel.update(it)
        }

        Toast.makeText(this, "Penyewaan berhasil!", Toast.LENGTH_LONG).show()
        finish() // Kembali ke layar sebelumnya (CustomerActivity)
    }

    /**
     * Menangani klik pada tombol kembali di ActionBar.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * Companion object untuk menyimpan konstanta yang dapat diakses secara global.
     */
    companion object {
        const val EXTRA_GADGET_ID = "extra_gadget_id"
    }
}