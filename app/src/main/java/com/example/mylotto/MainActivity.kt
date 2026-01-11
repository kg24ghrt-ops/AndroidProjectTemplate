package com.example.mylotto

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylotto.data.AppDatabase
import com.example.mylotto.databinding.ActivityMainBinding
import com.example.mylotto.ui.PickAdapter
import com.example.mylotto.viewmodel.PickViewModel

class MainActivity : AppCompatActivity() {

    // Properties
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PickViewModel
    private lateinit var pickAdapter: PickAdapter

    // 2D Lottery Categories (Strings are pulled from strings.xml)
    private val categories by lazy {
        listOf(
            getString(R.string.cat_direct) to "d",    // Direct (အပွင့်)
            getString(R.string.cat_reverse) to "r",   // Reverse (အာ)
            getString(R.string.cat_brake) to "b",     // Brake (ဘရိတ်)
            getString(R.string.cat_power) to "p",     // Power (ပါဝါ)
            getString(R.string.cat_natkhat) to "n",   // Nat Khat (နက္ခတ်)
            getString(R.string.cat_front) to "f",     // Front (ထိပ်စည်း)
            getString(R.string.cat_tail) to "g",      // Tail (နောက်ပိတ်)
            getString(R.string.cat_running) to "t",    // Running (ပတ်သီး)
            getString(R.string.cat_twins) to "a"      // Twins (အပူး)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. FORCE LIGHT MODE: Prevents Dark Mode from making text invisible
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        super.onCreate(savedInstanceState)

        // 2. INITIALIZE VIEW BINDING: Fixes 'setContentView' errors
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. INITIALIZE VIEWMODEL: Handles Database Logic
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
        // Setup Spinner (Category Selector)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories.map { it.first }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = spinnerAdapter

        // Setup RecyclerView (Recent Entries List)
        pickAdapter = PickAdapter(emptyList())
        binding.rvPicks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pickAdapter
        }

        // SAVE BUTTON: Logic for 2D Entry
        binding.btnSave.setOnClickListener {
            handleSave()
        }

        // SUMMARY BUTTON: Open Results
        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    private fun setupObservers() {
        // Watch the database for changes and update the list automatically
        viewModel.allPicks.observe(this) { picks ->
            pickAdapter.updateData(picks)
        }
    }

    private fun handleSave() {
        val name = binding.etName.text.toString().trim()
        val num = binding.etNumber.text.toString().trim()
        val amt = binding.etAmount.text.toString().trim()
        
        // Correct access to the selected Spinner item
        val selectedIdx = binding.spCategory.selectedItemPosition
        val categoryCode = categories[selectedIdx].second

        if (name.isNotEmpty() && num.isNotEmpty()) {
            // Check for valid 2D number length if it's a Direct pick
            if (categoryCode == "d" && num.length > 2) {
                Toast.makeText(this, "2D requires 2 digits only", Toast.LENGTH_SHORT).show()
                return
            }

            // Save to Database (Type is hardcoded to "2D")
            viewModel.addPick(name, num, "2D", categoryCode, amt)
            
            // Clear inputs for next entry
            binding.etNumber.text?.clear()
            binding.etAmount.text?.clear()
            Toast.makeText(this, "2D Entry Saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter Name and Number", Toast.LENGTH_SHORT).show()
        }
    }
}