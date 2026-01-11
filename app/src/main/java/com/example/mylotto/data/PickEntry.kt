package com.example.mylotto.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pick_entries")
data class PickEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,
    val lotteryNumber: String, // This holds the grouped list (e.g., "11, 12, 13...")
    val lotteryType: String,
    val categoryCode: String,
    val amount: String,        // The total multiplied amount
    val timestamp: Long = System.currentTimeMillis()
)