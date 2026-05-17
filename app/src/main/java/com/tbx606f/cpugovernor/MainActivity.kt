package com.tbx606f.cpugovernor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbx606f.cpugovernor.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter = CoreAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupProfileButtons()
        setupThermalToggle()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerCores.layoutManager = LinearLayoutManager(this)
        binding.recyclerCores.adapter = adapter
    }

    private fun setupProfileButtons() {
        binding.btnBattery.setOnClickListener { viewModel.applyProfile(Profiles.BATTERY_SAVER) }
        binding.btnEco.setOnClickListener { viewModel.applyProfile(Profiles.ECO) }
        binding.btnBalanced.setOnClickListener { viewModel.applyProfile(Profiles.BALANCED) }
        binding.btnPerformance.setOnClickListener { viewModel.applyProfile(Profiles.PERFORMANCE) }
    }

    private fun setupThermalToggle() {
        binding.switchThermal.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleThermal(isChecked)
            val msg = if (isChecked) "Thermal throttling DISABLED (tablet may get warm)" 
                      else "Thermal throttling re-enabled"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedProfile.collect { profile ->
                profile?.let {
                    Toast.makeText(this@MainActivity, "${it.name} applied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
