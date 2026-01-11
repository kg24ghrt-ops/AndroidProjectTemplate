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

        binding.rvResults.layoutManager = LinearLayoutManager(this)
        val adapter = PickAdapter(emptyList())
        binding.rvResults.adapter = adapter

        viewModel.allPicks.observe(this) { picks ->
            adapter.updateData(picks)
            binding.tvSummaryText.text = "Total Entries: ${picks.size}"
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}