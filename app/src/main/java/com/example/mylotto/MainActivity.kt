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

    /**
     * Maps the 2D classification codes to localized strings.
     * This ensures the Spinner shows Myanmar or English based on settings.
     */
    private fun getLocalizedCategories(): List<Pair<String, String>> {
        return listOf(
            getString(R.string.cat_direct) to "d",
            getString(R.string.cat_reverse) to "r",
            getString(R.string.cat_brake) to "b",
            getString(R.string.cat_power) to "p",
            getString(R.string.cat_natkhat) to "n",
            getString(R.string.cat_front) to "f",
            getString(R.string.cat_tail) to "g",
            getString(R.string.cat_running) to "t",
            getString(R.string.cat_twins) to "a",
            getString(R.string.cat_brother) to "z",
            getString(R.string.cat_akhway) to "k",
            getString(R.string.cat_akhway_twins) to "e",
            getString(R.string.cat_sone_sone) to "c",
            getString(R.string.cat_ma_ma) to "v",
            getString(R.string.cat_ma_sone) to "u",
            getString(R.string.cat_sone_ma) to "y"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Fix: Force Light Mode to avoid UI "Black on Black" text issues
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Database and ViewModel
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
        // Language Switcher
        binding.btnLanguageToggle.setOnClickListener { toggleLanguage() }

        updateSpinner()

        // Setup RecyclerView for Voucher display
        pickAdapter = PickAdapter(emptyList())
        binding.rvPicks.layoutManager = LinearLayoutManager(this)
        binding.rvPicks.adapter = pickAdapter

        // Action Buttons
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

    /**
     * Core Logic: Handles dynamic number expansion and money calculation.
     */
    private fun handleSave() {
        val name = binding.etName.text.toString().trim()
        val inputNum = binding.etNumber.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        
        val categories = getLocalizedCategories()
        val selectedIdx = binding.spCategory.selectedItemPosition
        val categoryCode = categories[selectedIdx].second

        if (name.isNotEmpty() && inputNum.isNotEmpty() && amountStr.isNotEmpty()) {
            val baseAmount = amountStr.toLongOrNull() ?: 0L
            
            // 1. Generate every single number for the voucher (e.g., A-khway logic)
            val expansion = LotteryEngine.expand(inputNum, categoryCode)
            
            // 2. Automatic Calculation: Base Amount x Total Numbers
            val totalCost = baseAmount * expansion.multiplier

            // 3. Save as a SINGLE Grouped Voucher Entry
            viewModel.addPick(
                name = name,
                number = expansion.printableList, // Saves "11, 12, 13..."
                type = "2D",
                category = getString(getCategoryNameRes(categoryCode)),
                amount = totalCost.toString()
            )

            // Clear Input fields for next entry
            binding.etNumber.text?.clear()
            binding.etAmount.text?.clear()

            // Feedback
            val msg = "Saved ${expansion.multiplier} numbers. Total: $totalCost Ks"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Maps the internal code back to the string resource for database logging.
     */
    private fun getCategoryNameRes(code: String): Int {
        return when(code) {
            "d" -> R.string.cat_direct
            "r" -> R.string.cat_reverse
            "b" -> R.string.cat_brake
            "p" -> R.string.cat_power
            "n" -> R.string.cat_natkhat
            "f" -> R.string.cat_front
            "g" -> R.string.cat_tail
            "t" -> R.string.cat_running
            "a" -> R.string.cat_twins
            "z" -> R.string.cat_brother
            "k" -> R.string.cat_akhway
            "e" -> R.string.cat_akhway_twins
            "c" -> R.string.cat_sone_sone
            "v" -> R.string.cat_ma_ma
            "u" -> R.string.cat_ma_sone
            "y" -> R.string.cat_sone_ma
            else -> R.string.cat_direct
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
        
        // Refresh activity to apply language changes
        finish()
        startActivity(intent)
    }

    private fun setupObservers() {
        viewModel.allPicks.observe(this) { pickAdapter.updateData(it) }
    }
}