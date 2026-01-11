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

    // Full 2D Taxonomy mapped to localized string resources
    private fun getLocalizedCategories(): List<Pair<String, String>> {
        return listOf(
            getString(R.string.cat_direct) to "d",    // Direct (ဒွဲ)
            getString(R.string.cat_reverse) to "r",   // Ar (အာ)
            getString(R.string.cat_brake) to "b",     // Brake (ဘရိတ်)
            getString(R.string.cat_power) to "p",     // Power (ပါဝါ)
            getString(R.string.cat_natkhat) to "n",   // Nat Khat (နက္ခတ်)
            getString(R.string.cat_front) to "f",     // Hteik (ထိပ်စည်း)
            getString(R.string.cat_tail) to "g",      // Nauk (နောက်ပိတ်)
            getString(R.string.cat_running) to "t",    // Pat-thee (ပတ်သီး)
            getString(R.string.cat_twins) to "a",     // A-puu (အပူး)
            getString(R.string.cat_brother) to "z",   // Nyi-Ko (ညီကို)
            getString(R.string.cat_akhway) to "k",    // A-khway (အခွေ)
            getString(R.string.cat_akhway_twins) to "e", // A-khway-puu (အခွေပူး)
            getString(R.string.cat_sone_sone) to "c", // Even-Even (စုံစုံ)
            getString(R.string.cat_ma_ma) to "v",     // Odd-Odd (မမ)
            getString(R.string.cat_ma_sone) to "u",   // Odd-Even (မစုံ)
            getString(R.string.cat_sone_ma) to "y"    // Even-Odd (စုံမ)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Fix: Force Light Mode to prevent UI colors from breaking
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
            
            // 1. Logic Engine generates all related 2D numbers
            val numbersToSave = LotteryEngine.expandCode(inputNum, categoryCode)
            
            // 2. Automatic Money Multiplication
            val totalCost = baseAmount * numbersToSave.size

            // 3. Save each related number as a separate entry
            for (num in numbersToSave) {
                viewModel.addPick(name, num, "2D", categoryCode, baseAmount.toString())
            }

            binding.etNumber.text?.clear()
            binding.etAmount.text?.clear()

            // Toast feedback shows how many numbers were generated and total cost
            val msg = "Saved ${numbersToSave.size} numbers. Total: $totalCost"
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
        
        // Restart activity to apply language update
        finish()
        startActivity(intent)
    }

    private fun setupObservers() {
        viewModel.allPicks.observe(this) { pickAdapter.updateData(it) }
    }
}