package com.example.mylotto.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mylotto.R
import com.example.mylotto.data.PickEntry
import java.text.SimpleDateFormat
import java.util.*

class PickAdapter(private var list: List<PickEntry>) : RecyclerView.Adapter<PickAdapter.PickViewHolder>() {

    class PickViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInfo: TextView = view.findViewById(R.id.tvInfo)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pick, parent, false)
        return PickViewHolder(view)
    }

    override fun onBindViewHolder(holder: PickViewHolder, position: Int) {
        val item = list[position]
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.timestamp))
        
        // Display logic: "Name - [Code][Cat] - [Type]"
        holder.tvInfo.text = "${item.personName} - ${item.numberCode}${item.categoryCode} (${item.lotteryType})"
        holder.tvAmount.text = item.amountText
        holder.tvDate.text = date
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<PickEntry>) {
        this.list = newList
        notifyDataSetChanged()
    }
}