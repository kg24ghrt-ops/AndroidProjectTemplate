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

    // Use 'by lazy' so getString() is called only after the Activity is attached to a context
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
        
        // 1. Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Initialize ViewModel with Factory
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
        // Spinner Setup using localized strings
        val spinnerAdapter = ArrayAdapter(
            this, 
            android.R.layout.simple_spinner_item, 
            categories.map { it.first }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = spinnerAdapter

        // RecyclerView Setup
        pickAdapter = PickAdapter(emptyList())
        binding.rvPicks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pickAdapter
        }

        // Save Button Logic
        binding.btnSave.setOnClickListener {
            saveEntry()
        }

        // Summary Button Logic (Navigate to ResultActivity)
        binding.btnSummary.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.allPicks.observe(this) { picks ->
            pickAdapter.updateData(picks)
        }
    }

    private fun saveEntry() {
        val name = binding.etName.text.toString().trim()
        val num = binding.etNumber.text.toString().trim()
        val amt = binding.etAmount.text.toString().trim()
        
        // Safely access the selected category code
        val selectedIndex = binding.spCategory.selectedItemPosition
        val catCode = categories[selectedIndex].second

        if (name.isNotEmpty() && num.isNotEmpty()) {
            // "2D" is used as the default type here; can be made dynamic later
            viewModel.addPick(name, num, "2D", catCode, amt)
            
            // Clear inputs for the next entry
            binding.etNumber.text?.clear()
            binding.etAmount.text?.clear()
            
            Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter Name and Number", Toast.LENGTH_SHORT).show()
        }
    }
}