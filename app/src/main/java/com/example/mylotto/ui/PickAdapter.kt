package com.example.mylotto.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mylotto.data.Pick
import com.example.mylotto.databinding.ItemPickBinding

class PickAdapter(private var picks: List<Pick>) : RecyclerView.Adapter<PickAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPickBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pick = picks[position]
        holder.binding.apply {
            tvName.text = pick.name
            tvNumbersList.text = pick.number // This is the comma-separated list
            tvTotalAmount.text = "${pick.amount} Ks"
            tvCategory.text = pick.category
        }
    }

    override fun getItemCount() = picks.size

    fun updateData(newPicks: List<Pick>) {
        this.picks = newPicks
        notifyDataSetChanged()
    }
}