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

    // Complete Taxonomy from Research Paper
    private val categories = listOf(
        "Direct (အပွင့်)" to "d",
        "ဘရိတ် (Brake)" to "b",
        "ပါဝါ (Power)" to "p",
        "နက္ခတ် (Nat Khat)" to "n",
        "အာ (Reverse)" to "r",
        "ထိပ်စည်း (Front)" to "f",
        "နောက်ပိတ် (Tail)" to "g",
        "ပတ်သီး (Running)" to "t",
        "အပူး (Twins)" to "a",
        "ညီကို (Brother)" to "z",
        "စုံစုံ (Even-Even)" to "c",
        "မမ (Odd-Odd)" to "v",
        "မစုံ (Odd-Even)" to "u",
        "စုံမ (Even-Odd)" to "y"
    )

    private val lotteryTypes = listOf("2D AM", "2D PM", "3D")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).pickDao()
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PickViewModel(dao) as T
            }
        })[PickViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        // 1. Category Spinner Setup
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.first })
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = catAdapter

        // 2. Lottery Type Spinner (Not in your previous code, but required by specs)
        // Note: Ensure you have a Spinner with ID spType in your activity_main.xml or use a default
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lotteryTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // If spType isn't in XML yet, this line might need an XML update
        // binding.spType.adapter = typeAdapter 

        // 3. RecyclerView Setup
        adapter = PickAdapter(emptyList())
        binding.rvPicks.layoutManager = LinearLayoutManager(this)
        binding.rvPicks.adapter = adapter

        // 4. Observe Data
        viewModel.allPicks.observe(this) { picks ->
            adapter.updateData(picks)
        }

        // 5. Save Action
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val num = binding.etNumber.text.toString().trim()
            val amt = binding.etAmount.text.toString().trim()
            val catCode = categories[binding.spCategory.selectedItemPosition].second
            val type = lotteryTypes[0] // Default to 2D AM, or bind to a spinner

            if (name.isNotEmpty() && num.isNotEmpty()) {
                viewModel.addPick(name, num, type, catCode, amt)
                
                // Clear fields for next entry
                binding.etNumber.text?.clear()
                binding.etAmount.text?.clear()
                Toast.makeText(this, "Saved (သိမ်းဆည်းပြီး)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fill Name and Number", Toast.LENGTH_SHORT).show()
            }
        }

        // 6. Navigation to Result/Summary Screen
        binding.btnSummary.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }
}