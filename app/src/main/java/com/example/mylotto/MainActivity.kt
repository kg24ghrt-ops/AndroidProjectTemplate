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
    private lateinit var pickAdapter: PickAdapter // Changed to lateinit var

    private val categories = listOf("Direct" to "d", "Brake" to "b", "Power" to "p")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // FIXED: Uses binding.root

        val dao = AppDatabase.getDatabase(this).pickDao()
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PickViewModel(dao) as T
            }
        })[PickViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        // Spinner
        val sAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.first })
        binding.spCategory.adapter = sAdapter

        // RecyclerView
        pickAdapter = PickAdapter(emptyList()) // FIXED: Assignment works on var
        binding.rvPicks.layoutManager = LinearLayoutManager(this)
        binding.rvPicks.adapter = pickAdapter

        viewModel.allPicks.observe(this) { pickAdapter.updateData(it) }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val num = binding.etNumber.text.toString()
            val selectedCode = categories[binding.spCategory.selectedItemPosition].second // FIXED access
            
            if (name.isNotEmpty() && num.isNotEmpty()) {
                viewModel.addPick(name, num, "2D", selectedCode, binding.etAmount.text.toString())
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }
}