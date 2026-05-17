package com.tbx606f.cpugovernor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tbx606f.cpugovernor.databinding.ItemCoreBinding

class CoreAdapter : RecyclerView.Adapter<CoreAdapter.ViewHolder>() {

    private val cores = (0..7).toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cores[position])
    }

    override fun getItemCount() = cores.size

    class ViewHolder(private val binding: ItemCoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(core: Int) {
            binding.textCore.text = "CPU $core"
        }
    }
}
