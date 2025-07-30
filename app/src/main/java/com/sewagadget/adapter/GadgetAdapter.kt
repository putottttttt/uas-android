package com.sewagadget.adapter

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load // <-- IMPORT PENTING
import com.sewagadget.R // <-- IMPORT PENTING
import com.sewagadget.data.model.Gadget
import com.sewagadget.databinding.ItemGadgetBinding
import java.text.NumberFormat
import java.util.Locale

class GadgetAdapter(
    private val userRole: String,
    private val onItemClick: (Gadget) -> Unit,
    private val onEditClick: (Gadget) -> Unit,
    private val onDeleteClick: (Gadget) -> Unit
) : ListAdapter<Gadget, GadgetAdapter.GadgetViewHolder>(GADGET_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GadgetViewHolder {
        val binding = ItemGadgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GadgetViewHolder(binding, onItemClick, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: GadgetViewHolder, position: Int) {
        val currentGadget = getItem(position)
        holder.bind(currentGadget, userRole)
    }

    class GadgetViewHolder(
        private val binding: ItemGadgetBinding,
        private val onItemClick: (Gadget) -> Unit,
        private val onEditClick: (Gadget) -> Unit,
        private val onDeleteClick: (Gadget) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentGadget: Gadget? = null

        init {
            itemView.setOnClickListener {
                currentGadget?.let { onItemClick(it) }
            }
            binding.btnEdit.setOnClickListener {
                currentGadget?.let { onEditClick(it) }
            }
            binding.btnDelete.setOnClickListener {
                currentGadget?.let { onDeleteClick(it) }
            }
        }

        fun bind(gadget: Gadget, role: String) {
            currentGadget = gadget
            binding.tvGadgetName.text = gadget.name
            binding.tvGadgetBrand.text = gadget.brand
            binding.tvGadgetSpecs.text = gadget.specs

            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvGadgetPrice.text = "${formatter.format(gadget.pricePerDay)} / hari"

            if (gadget.isAvailable) {
                binding.tvGadgetStatus.text = "Tersedia"
                binding.tvGadgetStatus.setBackgroundColor(Color.parseColor("#4CAF50"))
            } else {
                binding.tvGadgetStatus.text = "Disewa"
                binding.tvGadgetStatus.setBackgroundColor(Color.parseColor("#F44336"))
            }

            if (gadget.imageUri != null) {
                binding.ivListGadget.load(Uri.parse(gadget.imageUri)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background) // Gambar default saat loading
                    error(R.drawable.ic_launcher_foreground) // Gambar jika terjadi error
                }
            } else {
                binding.ivListGadget.setImageResource(R.mipmap.ic_launcher) // Gambar default jika URI null
            }

            if (role == "admin") {
                binding.layoutAdminActions.visibility = View.VISIBLE
            } else {
                binding.layoutAdminActions.visibility = View.GONE
            }
        }
    }

    companion object {
        private val GADGET_COMPARATOR = object : DiffUtil.ItemCallback<Gadget>() {
            override fun areItemsTheSame(oldItem: Gadget, newItem: Gadget): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Gadget, newItem: Gadget): Boolean {
                return oldItem == newItem
            }
        }
    }
}