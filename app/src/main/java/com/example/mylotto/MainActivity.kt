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
        "Direct" to "d", "Brake" to "b", "Power" to "p", "Nat Khat" to "n",
        "Reverse" to "r", "Front" to "f", "Tail" to "g", "Running" to "t", "Twins" to "a"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // FIX: Ensure we use binding.root
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).pickDao()
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PickViewModel(dao) as T
            }
        })[PickViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.first })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = spinnerAdapter

        // FIX: Initialize adapter correctly
        adapter = PickAdapter(emptyList())
        binding.rvPicks.layoutManager = LinearLayoutManager(this)
        binding.rvPicks.adapter = adapter

        viewModel.allPicks.observe(this) { picks ->
            adapter.updateData(picks)
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val num = binding.etNumber.text.toString()
            val amt = binding.etAmount.text.toString()
            // FIX: Access spinner selection correctly
            val catCode = categories[binding.spCategory.selectedItemPosition].second
            
            if (name.isNotEmpty() && num.isNotEmpty()) {
                viewModel.addPick(name, num, "2D", catCode, amt)
                binding.etNumber.text?.clear()
                binding.etAmount.text?.clear()
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }
}