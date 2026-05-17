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
        binding.btnBattery.setOnClickListener {
            viewModel.applyProfile(Profiles.BATTERY_SAVER)
            highlightButton(Profiles.BATTERY_SAVER)
        }
        binding.btnEco.setOnClickListener {
            viewModel.applyProfile(Profiles.ECO)
            highlightButton(Profiles.ECO)
        }
        binding.btnBalanced.setOnClickListener {
            viewModel.applyProfile(Profiles.BALANCED)
            highlightButton(Profiles.BALANCED)
        }
        binding.btnPerformance.setOnClickListener {
            viewModel.applyProfile(Profiles.PERFORMANCE)
            highlightButton(Profiles.PERFORMANCE)
        }
    }

    private fun setupThermalToggle() {
        binding.switchThermal.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleThermal(isChecked)
            if (isChecked) {
                binding.tvThermalStatus.text = "ENABLED  ·  /proc/mtk_thermal"
                Toast.makeText(this, "Thermal throttling re-enabled", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvThermalStatus.text = "DISABLED  ·  /proc/mtk_thermal"
                Toast.makeText(this, "Thermal throttling DISABLED — tablet may get warm", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedProfile.collect { profile ->
                profile ?: return@collect
                binding.tvActiveProfile.text =
                    "${profile.name.uppercase()}  ·  ${profile.governor}  ·  ${profile.maxFreq / 1000} MHz"
                binding.tvStatGovernor.text = profile.governor
                binding.tvStatMaxFreq.text  = "${profile.maxFreq / 1000}MHz"
                binding.tvStatDrain.text    = profile.estimatedDrain
                Toast.makeText(this@MainActivity, "${profile.name} applied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun highlightButton(active: PowerProfile) {
        val on  = resources.getDrawable(R.drawable.bg_profile_btn_active, theme)
        val off = resources.getDrawable(R.drawable.bg_profile_btn, theme)
        binding.btnBattery.background     = if (active == Profiles.BATTERY_SAVER) on else off
        binding.btnEco.background         = if (active == Profiles.ECO) on else off
        binding.btnBalanced.background    = if (active == Profiles.BALANCED) on else off
        binding.btnPerformance.background = if (active == Profiles.PERFORMANCE) on else off
    }
}
