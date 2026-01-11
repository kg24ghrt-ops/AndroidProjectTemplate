package com.example.mylotto

import android.content.Context
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
import com.example.mylotto.ui.PickAdapter
import com.example.mylotto.viewmodel.PickViewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PickViewModel
    private lateinit var pickAdapter: PickAdapter

    // The categories must be re-fetched after a language change
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
            getString(R.string.cat_twins) to "a"
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
        // --- Language Toggle Button ---
        binding.btnLanguageToggle.setOnClickListener {
            toggleLanguage()
        }

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
        
        // Update the context with new locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        
        // Restart activity to apply changes globally
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun handleSave() {
        val name = binding.etName.text.toString().trim()
        val num = binding.etNumber.text.toString().trim()
        val amt = binding.etAmount.text.toString().trim()
        
        val categories = getLocalizedCategories()
        val selectedIdx = binding.spCategory.selectedItemPosition
        val categoryCode = categories[selectedIdx].second

        if (name.isNotEmpty() && num.isNotEmpty()) {
            viewModel.addPick(name, num, "2D", categoryCode, amt)
            binding.etNumber.text?.clear()
            binding.etAmount.text?.clear()
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.allPicks.observe(this) { pickAdapter.updateData(it) }
    }
}