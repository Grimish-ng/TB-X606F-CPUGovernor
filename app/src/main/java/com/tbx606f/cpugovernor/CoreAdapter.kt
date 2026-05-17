package com.tbx606f.cpugovernor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CoreAdapter : RecyclerView.Adapter<CoreAdapter.CoreViewHolder>() {

    data class CoreRow(
        val label: String,        // "CPU0"
        val governor: String,     // "schedutil" or "offline"
        val freqLabel: String,    // "1200 MHz" or "idle"
        val progressPct: Int      // 0-100
    )

    private var rows: List<CoreRow> = emptyList()

    fun submitRows(newRows: List<CoreRow>) {
        rows = newRows
        notifyDataSetChanged()
    }

    inner class CoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textCore: TextView        = view.findViewById(R.id.textCore)
        val textGovernor: TextView    = view.findViewById(R.id.textGovernor)
        val textFreq: TextView        = view.findViewById(R.id.textFreq)
        val progressFreq: ProgressBar = view.findViewById(R.id.progressFreq)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_core, parent, false)
        return CoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoreViewHolder, position: Int) {
        val row = rows[position]
        holder.textCore.text          = row.label
        holder.textGovernor.text      = row.governor
        holder.textFreq.text          = row.freqLabel
        holder.progressFreq.progress  = row.progressPct
    }

    override fun getItemCount() = rows.size
}
