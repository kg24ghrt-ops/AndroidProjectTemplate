package com.example.mylotto.viewmodel

import androidx.lifecycle.*
import com.example.mylotto.data.PickDao
import com.example.mylotto.data.PickEntry
import kotlinx.coroutines.launch

class PickViewModel(private val dao: PickDao) : ViewModel() {

    val allPicks: LiveData<List<PickEntry>> = dao.getAllPicks().asLiveData()

    fun addPick(name: String, num: String, type: String, cat: String, amt: String) {
        viewModelScope.launch {
            val entry = PickEntry(
                personName = name,
                numberCode = num,
                lotteryType = type,
                categoryCode = cat,
                amountText = amt
            )
            dao.insertPick(entry)
        }
    }
}