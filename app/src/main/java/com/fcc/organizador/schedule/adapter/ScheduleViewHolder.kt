package com.fcc.organizador.schedule.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fcc.organizador.databinding.ItemScheduleBinding
import com.fcc.organizador.schedule.Schedule

class ScheduleViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemScheduleBinding.bind(view)

    fun render(schedule: Schedule, onClickListener: (Schedule) -> Unit){
        binding.scheduleCellText.text = schedule.text

        binding.scheduleCard.setOnClickListener { onClickListener(schedule) }

    }
}