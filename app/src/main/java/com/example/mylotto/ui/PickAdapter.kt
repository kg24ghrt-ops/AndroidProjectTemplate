package com.example.mylotto.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mylotto.data.PickEntry // Changed from Pick
import com.example.mylotto.databinding.ItemPickBinding

class PickAdapter(private var picks: List<PickEntry>) : RecyclerView.Adapter<PickAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPickBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pick = picks[position]
        
        holder.binding.apply {
            // Display the Person's Name
            tvName.text = pick.personName
            
            // This is the grouped list (e.g., 11, 12, 13, 14, 15...) 
            // displayed directly under the name
            tvNumbersList.text = pick.lotteryNumber 
            
            // Display the Total Multiplied Amount
            tvTotalAmount.text = "${pick.amount} Ks"
            
            // Display the Category Code (e.g., A-Khway)
            tvCategory.text = pick.categoryCode
        }
    }

    override fun getItemCount() = picks.size

    fun updateData(newPicks: List<PickEntry>) {
        this.picks = newPicks
        notifyDataSetChanged()
    }
}