package com.sewagadget.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sewagadget.data.model.TransactionWithGadget
import com.sewagadget.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.*

class TransactionAdapter : ListAdapter<TransactionWithGadget, TransactionAdapter.TransactionViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TransactionWithGadget) {
            binding.tvGadgetName.text = data.gadget.name
            binding.tvRentalDates.text = "${data.transaction.startDate} - ${data.transaction.endDate}"

            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvTotalPrice.text = "Total: ${formatter.format(data.transaction.totalPrice)}"

            binding.tvTransactionStatus.text = data.transaction.status.replaceFirstChar { it.titlecase() }
            if (data.transaction.status.equals("disewa", ignoreCase = true)) {
                binding.tvTransactionStatus.setBackgroundColor(Color.parseColor("#FF9800")) // Orange
            } else {
                binding.tvTransactionStatus.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<TransactionWithGadget>() {
            override fun areItemsTheSame(oldItem: TransactionWithGadget, newItem: TransactionWithGadget): Boolean {
                return oldItem.transaction.id == newItem.transaction.id
            }

            override fun areContentsTheSame(oldItem: TransactionWithGadget, newItem: TransactionWithGadget): Boolean {
                return oldItem == newItem
            }
        }
    }
}