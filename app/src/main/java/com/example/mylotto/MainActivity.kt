package com.example.mylotto

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
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
        binding.rvPicks.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = false
            stackFromEnd = false
        }
        binding.rvPicks.adapter = pickAdapter

        binding.btnSave.setOnClickListener { 
            handleSave() 
            hideKeyboard() 
        }

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
        val amountStr = binding.etAmount.text.toString().trim()
        
        val categories = getLocalizedCategories()
        val selectedIdx = binding.spCategory.selectedItemPosition
        val categoryCode = categories[selectedIdx].second

        // Basic check for Name and Amount
        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill Name and Amount", Toast.LENGTH_SHORT).show()
            return
        }

        val baseAmount = amountStr.toLongOrNull() ?: 0L
        if (baseAmount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Process through the Engine
        val result = LotteryEngine.expand(inputNum, categoryCode)

        // Handle the Sealed Class states (Success vs Invalid)
        when (result) {
            is LotteryEngine.ExpansionResult.Success -> {
                val totalCost = baseAmount * result.multiplier

                viewModel.addPick(
                    name = name,
                    num = result.printableList, 
                    type = "2D",
                    cat = getString(getCategoryNameRes(categoryCode)),
                    amt = totalCost.toString()
                )

                // Success cleanup
                binding.etNumber.text?.clear()
                binding.etAmount.text?.clear()
                binding.etNumber.error = null 

                Toast.makeText(this, "Saved ${result.multiplier} numbers. Total: $totalCost Ks", Toast.LENGTH_LONG).show()
            }
            is LotteryEngine.ExpansionResult.Invalid -> {
                // Display specific validation error from the engine
                binding.etNumber.error = result.message
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

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
            "v" -> R.string.cat_ma_ ma
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
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        
        finish()
        startActivity(intent)
    }

    private fun setupObservers() {
        viewModel.allPicks.observe(this) { 
            pickAdapter.updateData(it) 
            if (it.isNotEmpty()) binding.rvPicks.scrollToPosition(0)
        }
    }
}