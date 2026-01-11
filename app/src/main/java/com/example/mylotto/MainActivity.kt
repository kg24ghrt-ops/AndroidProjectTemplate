package com.example.mylotto

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylotto.data.AppDatabase
import com.example.mylotto.databinding.ActivityMainBinding
import com.example.mylotto.ui.PickAdapter
import com.example.mylotto.viewmodel.PickViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PickViewModel
    private lateinit var adapter: PickAdapter

    private val categories = listOf(
        "Direct" to "d",
        "ဘရိတ် (Brake)" to "b",
        "ပါဝါ (Power)" to "p",
        "နက္ခတ် (Nat Khat)" to "n",
        "အာ (Reverse)" to "r",
        "ထိပ်စည်း (Front)" to "f",
        "နောက်ပိတ် (Tail)" to "g",
        "ပတ်သီး (Running)" to "t",
        "အပူး (Twins)" to "a"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Use ViewBinding to inflate layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).pickDao()
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PickViewModel(dao) as T
            }
        })[PickViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        // Spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.first })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = spinnerAdapter

        // RecyclerView
        adapter = PickAdapter(emptyList())
        binding.rvPicks.layoutManager = LinearLayoutManager(this)
        binding.rvPicks.adapter = adapter

        // Data Observation
        viewModel.allPicks.observe(this) { picks ->
            adapter.updateData(picks)
        }

        // Save Click
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val num = binding.etNumber.text.toString()
            val amt = binding.etAmount.text.toString()
            val catCode = categories[binding.spCategory.selectedItemPosition].second
            
            if (name.isNotEmpty() && num.isNotEmpty()) {
                viewModel.addPick(name, num, "2D", catCode, amt)
                binding.etNumber.text?.clear()
                binding.etAmount.text?.clear()
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

        // Summary Click - Navigate to ResultActivity
        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }
}