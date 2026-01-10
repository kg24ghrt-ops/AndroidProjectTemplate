package com.example.mylotto

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylotto.data.AppDatabase
import com.example.mylotto.ui.PickAdapter
import com.example.mylotto.viewmodel.PickViewModel
import com.example.mylotto.viewmodel.PickViewModelFactory // See note below

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PickViewModel
    private lateinit var adapter: PickAdapter
    
    // Mapping for Spinner: Display Name to Code
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
        setContentView(R.layout.activity_main)

        val dao = AppDatabase.getDatabase(this).pickDao()
        // Simple ViewModelProvider for demonstration
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PickViewModel(dao) as T
            }
        })[PickViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        val etName = findViewById<EditText>(R.id.etName)
        val etNumber = findViewById<EditText>(R.id.etNumber)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val spCategory = findViewById<Spinner>(R.id.spCategory)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnSummary = findViewById<Button>(R.id.btnSummary) // Add to XML
        val rvPicks = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPicks)

        // Spinner Setup
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.first })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = spinnerAdapter

        // RecyclerView Setup
        adapter = PickAdapter(emptyList())
        rvPicks.layoutManager = LinearLayoutManager(this)
        rvPicks.adapter = adapter

        // Observers
        viewModel.allPicks.observe(this) { picks ->
            adapter.updateData(picks)
        }

        // Actions
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val num = etNumber.text.toString()
            val amt = etAmount.text.toString()
            val catCode = categories[spCategory.selectedItemPosition].second
            
            if (name.isNotEmpty() && num.isNotEmpty()) {
                viewModel.addPick(name, num, "2D", catCode, amt)
                etNumber.text.clear()
                etAmount.text.clear()
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

        btnSummary.setOnClickListener {
            showSummaryDialog()
        }
    }

    private fun showSummaryDialog() {
        val currentPicks = viewModel.allPicks.value ?: emptyList()
        
        val personTotals = currentPicks.groupingBy { it.personName }.eachCount()
        val categoryTotals = currentPicks.groupingBy { it.categoryCode }.eachCount()
        
        val summaryText = StringBuilder()
        summaryText.append("Total Picks: ${currentPicks.size}\n\n")
        summaryText.append("By Person:\n")
        personTotals.forEach { (name, count) -> summaryText.append("- $name: $count\n") }
        summaryText.append("\nBy Category:\n")
        categoryTotals.forEach { (code, count) -> summaryText.append("- $code: $count\n") }

        AlertDialog.Builder(this)
            .setTitle("Summary (အကျဉ်းချုပ်)")
            .setMessage(summaryText.toString())
            .setPositiveButton("OK", null)
            .show()
    }
}