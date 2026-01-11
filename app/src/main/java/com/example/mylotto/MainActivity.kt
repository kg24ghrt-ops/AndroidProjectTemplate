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
    private lateinit var pickAdapter: PickAdapter

    // Taxonomy mapping: Display name to Database Code
    // Initialized 'by lazy' to ensure it only runs after the Activity has a Context
    private val categories by lazy {
        listOf(
            getString(R.string.cat_direct) to "d",
            getString(R.string.cat_brake) to "b",
            getString(R.string.cat_power) to "p",
            getString(R.string.cat_natkhat) to "n",
            getString(R.string.cat_reverse) to "r",
            getString(R.string.cat_front) to "f",
            getString(R.string.cat_tail) to "g",
            getString(R.string.cat_running) to "t",
            getString(R.string.cat_twins) to "a"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Inflate ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Initialize ViewModel via Factory
        val dao = AppDatabase.getDatabase(this).pickDao()
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PickViewModel(dao) as T
            }
        })[PickViewModel::class.java]

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // Spinner: Setup with localized category names
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories.map { it.first }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = spinnerAdapter

        // RecyclerView: Initialize with empty list
        pickAdapter = PickAdapter(emptyList())
        binding.rvPicks.layoutManager = LinearLayoutManager(this)
        binding.rvPicks.adapter = pickAdapter

        // Button: Save Entry
        binding.btnSave.setOnClickListener {
            saveData()
        }

        // Button: Open Summary Activity
        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    private fun setupObservers() {
        // Observe Database changes and update RecyclerView
        viewModel.allPicks.observe(this) { picks ->
            pickAdapter.updateData(picks)
        }
    }

    private fun saveData() {
        val name = binding.etName.text.toString().trim()
        val num = binding.etNumber.text.toString().trim()
        val amt = binding.etAmount.text.toString().trim()
        
        // Get the selection index from Spinner
        val selectedIdx = binding.spCategory.selectedItemPosition
        val categoryCode = categories[selectedIdx].second

        if (name.isNotEmpty() && num.isNotEmpty()) {
            // Add to database via ViewModel
            viewModel.addPick(name, num, "2D", categoryCode, amt)
            
            // Clear inputs
            binding.etNumber.text?.clear()
            binding.etAmount.text?.clear()
            
            // Fixed: Literal string used to avoid "Unresolved reference" if XML isn't ready
            Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please fill in Name and Number", Toast.LENGTH_SHORT).show()
        }
    }
}