package com.example.mylotto.viewmodel

import androidx.lifecycle.*
import com.example.mylotto.data.PickDao
import com.example.mylotto.data.PickEntry
import kotlinx.coroutines.launch

class PickViewModel(private val dao: PickDao) : ViewModel() {

    val allPicks: LiveData<List<PickEntry>> = dao.getAllPicks().asLiveData()

    // Parameters are named: name, num, type, cat, amt
    fun addPick(name: String, num: String, type: String, cat: String, amt: String) {
        viewModelScope.launch {
            val entry = PickEntry(
                personName = name,
                lotteryNumber = num,
                lotteryType = type,
                categoryCode = cat,
                amount = amt
            )
            dao.insertPick(entry)
        }
    }
}