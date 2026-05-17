package com.tbx606f.cpugovernor

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.tbx606f.cpugovernor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val coreAdapter = CoreAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupProfileCards()
        observeViewModel()

        binding.btnApply.setOnClickListener {
            viewModel.applySelectedProfile()
        }

        binding.btnRetryRoot.setOnClickListener {
            viewModel.checkRoot()
        }
    }

    private fun setupRecyclerView() {
        binding.rvCores.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = coreAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupProfileCards() {
        val profileCardPairs = listOf(
            PowerProfiles.BATTERY_SAVER to binding.cardBatterySaver,
            PowerProfiles.ECO         to binding.cardEco,
            PowerProfiles.BALANCED    to binding.cardBalanced,
            PowerProfiles.PERFORMANCE to binding.cardPerformance
        )

        profileCardPairs.forEach { (profile, card) ->
            card.setOnClickListener {
                viewModel.selectProfile(profile)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.rootState.observe(this) { state ->
            when (state) {
                is MainViewModel.RootState.Checking -> {
                    binding.layoutRootChecking.visibility = View.VISIBLE
                    binding.layoutRootDenied.visibility = View.GONE
                    binding.layoutMain.visibility = View.GONE
                }
                is MainViewModel.RootState.Denied -> {
                    binding.layoutRootChecking.visibility = View.GONE
                    binding.layoutRootDenied.visibility = View.VISIBLE
                    binding.layoutMain.visibility = View.GONE
                }
                is MainViewModel.RootState.Granted -> {
                    binding.layoutRootChecking.visibility = View.GONE
                    binding.layoutRootDenied.visibility = View.GONE
                    binding.layoutMain.visibility = View.VISIBLE
                }
            }
        }

        viewModel.selectedProfile.observe(this) { profile ->
            highlightSelectedCard(profile)
            binding.tvSelectedGovernor.text = profile.governor
            binding.tvSelectedMaxFreq.text = "${profile.maxFreqMhz} MHz"
            binding.tvSelectedDrain.text = "${profile.estimatedDrainMin}–${profile.estimatedDrainMax}%/hr"
            binding.tvProfileDescription.text = profile.description
        }

        viewModel.activeProfile.observe(this) { profile ->
            binding.tvActiveProfile.text = if (profile != null) {
                "Active: ${profile.displayName}"
            } else {
                "Active: unknown"
            }
        }

        viewModel.coreInfos.observe(this) { cores ->
            if (cores.isNotEmpty()) {
                coreAdapter.submitList(cores.toList())
            }
        }

        viewModel.cpuTemp.observe(this) { temp ->
            if (temp > 0) {
                binding.tvCpuTemp.text = "${temp}°C"
                binding.tvCpuTemp.setTextColor(
                    when {
                        temp >= 75 -> ContextCompat.getColor(this, R.color.temp_hot)
                        temp >= 60 -> ContextCompat.getColor(this, R.color.temp_warm)
                        else       -> ContextCompat.getColor(this, R.color.temp_normal)
                    }
                )
            } else {
                binding.tvCpuTemp.text = "–°C"
            }
        }

        viewModel.applyState.observe(this) { state ->
            when (state) {
                is MainViewModel.ApplyState.Idle -> {
                    binding.btnApply.isEnabled = true
                    binding.btnApply.text = "Apply profile"
                }
                is MainViewModel.ApplyState.Applying -> {
                    binding.btnApply.isEnabled = false
                    binding.btnApply.text = "Applying…"
                }
                is MainViewModel.ApplyState.Success -> {
                    binding.btnApply.isEnabled = true
                    binding.btnApply.text = "✓ Applied: ${state.profile.displayName}"
                }
                is MainViewModel.ApplyState.Error -> {
                    binding.btnApply.isEnabled = true
                    binding.btnApply.text = "Apply profile"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun highlightSelectedCard(profile: PowerProfile) {
        val allCards = listOf(
            PowerProfiles.BATTERY_SAVER to binding.cardBatterySaver,
            PowerProfiles.ECO           to binding.cardEco,
            PowerProfiles.BALANCED      to binding.cardBalanced,
            PowerProfiles.PERFORMANCE   to binding.cardPerformance
        )
        allCards.forEach { (p, card) ->
            card.strokeWidth = if (p.id == profile.id) 4 else 0
            card.strokeColor = ContextCompat.getColor(this, R.color.profile_selected_stroke)
        }
    }
}
