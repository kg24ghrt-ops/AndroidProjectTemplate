package com.example.mylotto.viewmodel

import androidx.lifecycle.*
import com.example.mylotto.data.PickDao
import com.example.mylotto.data.PickEntry
import kotlinx.coroutines.launch

class PickViewModel(private val dao: PickDao) : ViewModel() {

    // Converts the Database Flow into LiveData for the Activity to observe
    val allPicks: LiveData<List<PickEntry>> = dao.getAllPicks().asLiveData()

    /**
     * Adds a single Voucher entry to the database.
     * @param name The player's name
     * @param num The expanded list of numbers (e.g., "11, 12, 13, 14, 15")
     * @param type "2D"
     * @param cat The category code or name (e.g., "A-Khway")
     * @param amt The total multiplied amount
     */
    fun addPick(name: String, num: String, type: String, cat: String, amt: String) {
        viewModelScope.launch {
            val entry = PickEntry(
                personName = name,
                lotteryNumber = num, // Ensure this matches PickEntry property name
                lotteryType = type,
                categoryCode = cat,
                amount = amt        // Ensure this matches PickEntry property name
            )
            dao.insertPick(entry)
        }
    }

    // Optional: Add a function to clear the ledger
    fun clearAllPicks() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}