package com.example.mylotto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylotto.data.AppDatabase
import com.example.mylotto.databinding.ActivityResultBinding
import com.example.mylotto.ui.PickAdapter
import com.example.mylotto.viewmodel.PickViewModel

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: PickViewModel
    private lateinit var adapter: PickAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).pickDao()
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PickViewModel(dao) as T
            }
        })[PickViewModel::class.java]

        setupView()
    }

    private fun setupView() {
        adapter = PickAdapter(emptyList())
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter

        viewModel.allPicks.observe(this) { picks ->
            adapter.updateData(picks)
            calculateSummary(picks)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun calculateSummary(picks: List<com.example.mylotto.data.PickEntry>) {
        val personGroup = picks.groupBy { it.personName }
        val categoryGroup = picks.groupBy { it.categoryCode }
        
        val summaryStr = StringBuilder()
        summaryStr.append("Total: ${picks.size} entries\n\n")
        summaryStr.append("--- By Person ---\n")
        personGroup.forEach { (name, list) -> summaryStr.append("$name: ${list.size}\n") }
        
        binding.tvSummaryText.text = summaryStr.toString()
    }
}