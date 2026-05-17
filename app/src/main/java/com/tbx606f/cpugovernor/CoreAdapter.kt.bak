package com.tbx606f.cpugovernor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CoreAdapter : ListAdapter<CpuGovernorManager.CoreInfo, CoreAdapter.CoreViewHolder>(CoreDiff()) {

    class CoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coreLabel: TextView = view.findViewById(R.id.core_label)
        val freqText: TextView = view.findViewById(R.id.freq_text)
        val governorText: TextView = view.findViewById(R.id.governor_text)
        val usageBar: ProgressBar = view.findViewById(R.id.usage_bar)
    }

    class CoreDiff : DiffUtil.ItemCallback<CpuGovernorManager.CoreInfo>() {
        override fun areItemsTheSame(a: CpuGovernorManager.CoreInfo, b: CpuGovernorManager.CoreInfo) = a.index == b.index
        override fun areContentsTheSame(a: CpuGovernorManager.CoreInfo, b: CpuGovernorManager.CoreInfo) = a == b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_core, parent, false)
        return CoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoreViewHolder, position: Int) {
        val core = getItem(position)
        holder.coreLabel.text = "Core ${core.index}"

        if (!core.online) {
            holder.freqText.text = "offline"
            holder.governorText.text = "—"
            holder.usageBar.progress = 0
            return
        }

        holder.governorText.text = core.governor

        if (core.currentFreqMhz > 0) {
            holder.freqText.text = "${core.currentFreqMhz} MHz"
            val pct = if (core.maxFreqMhz > 0) {
                ((core.currentFreqMhz.toFloat() / core.maxFreqMhz) * 100).toInt().coerceIn(0, 100)
            } else 0
            holder.usageBar.progress = pct
        } else {
            holder.freqText.text = "idle"
            holder.usageBar.progress = 0
        }
    }
}
