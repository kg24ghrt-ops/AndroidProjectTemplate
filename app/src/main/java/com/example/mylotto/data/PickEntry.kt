package com.example.mylotto.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pick_entries")
data class PickEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,
    val numberCode: String,      // e.g., "48" or "5"
    val lotteryType: String,    // "2D AM", "2D PM", "3D"
    val categoryCode: String,   // "b", "p", "n", "r", etc.
    val amountText: String,     // e.g., "200r", "1000"
    val timestamp: Long = System.currentTimeMillis()
)