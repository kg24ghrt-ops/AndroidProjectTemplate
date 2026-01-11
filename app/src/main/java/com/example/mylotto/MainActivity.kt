package com.example.mylotto

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylotto.data.AppDatabase
import com.example.mylotto.databinding.ActivityMainBinding
import com.example.mylotto.logic.LotteryEngine
import com.example.mylotto.ui.PickAdapter
import com.example.mylotto.viewmodel.PickViewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PickViewModel
    private lateinit var pickAdapter: PickAdapter

    // Taxonomy of 2D codes mapped to localized strings
    private fun getLocalizedCategories(): List<Pair<String, String>> {
        return listOf(
            getString(R.string.cat_direct) to "d",    // Direct
            getString(R.string.cat_reverse) to "r",   // Ar (Reverse)
            getString(R.string.cat_brake) to "b",     // Brake
            getString(R.string.cat_power) to "p",     // Power
            getString(R.string.cat_natkhat) to "n",   // Nat Khat
            getString(R.string.cat_front) to "f",     // Hteik
            getString(R.string.cat_tail) to "g",      // Nauk
            getString(R.string.cat_running) to "t",    // Pat-thee
            getString(R.string.cat_twins) to "a",     // A-puu
            "A-Khway" to "k",                         // Combination
            "Nyi-Ko" to "z"                           // Brother
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Fix: Force Light Mode to prevent "Desktop/Dark Mode break"
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.btnLanguageToggle.setOnClickListener { toggleLanguage() }

        updateSpinner()

        pickAdapter = PickAdapter(emptyList())
        binding.rvPicks.layoutManager = LinearLayoutManager(this)
        binding.rvPicks.adapter = pickAdapter

        binding.btnSave.setOnClickListener { handleSave() }
        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    private fun updateSpinner() {
        val categories = getLocalizedCategories()
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories.map { it.first }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = spinnerAdapter
    }

    private fun handleSave() {
        val name = binding.etName.text.toString().trim()
        val inputNum = binding.etNumber.text.toString().trim()
        val baseAmountStr = binding.etAmount.text.toString().trim()
        
        val categories = getLocalizedCategories()
        val selectedIdx = binding.spCategory.selectedItemPosition
        val categoryCode = categories[selectedIdx].second

        if (name.isNotEmpty() && inputNum.isNotEmpty() && baseAmountStr.isNotEmpty()) {
            val baseAmount = baseAmountStr.toLongOrNull() ?: 0L
            
            // Generate related numbers using the Logic Engine
            val numbersToSave = LotteryEngine.expandCode(inputNum, categoryCode)
            
            // Automatic Multiplication for the display toast
            val totalCost = baseAmount * numbersToSave.size

            // Save each number into the database
            for (num in numbersToSave) {
                viewModel.addPick(name, num, "2D", categoryCode, baseAmount.toString())
            }

            // Clear inputs
            binding.etNumber.text?.clear()
            binding.etAmount.text?.clear()

            // Feedback showing related number count and total money
            val msg = "Saved ${numbersToSave.size} numbers. Total Amt: $totalCost"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleLanguage() {
        val currentLocale = resources.configuration.locales[0].language
        val newLang = if (currentLocale == "my") "en" else "my"
        setLocale(newLang)
    }

    private fun setLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        
        // Restart to apply language changes
        finish()
        startActivity(intent)
    }

    private fun setupObservers() {
        viewModel.allPicks.observe(this) { pickAdapter.updateData(it) }
    }
}