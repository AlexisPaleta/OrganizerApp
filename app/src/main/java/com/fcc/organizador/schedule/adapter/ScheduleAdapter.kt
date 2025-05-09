package com.fcc.organizador.schedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcc.organizador.R
import com.fcc.organizador.schedule.Schedule

class ScheduleAdapter(
    private val scheduleList: List<Schedule>,
    private val onClickListener: (Schedule) -> Unit
): RecyclerView.Adapter<ScheduleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ScheduleViewHolder(layoutInflater.inflate(R.layout.item_schedule, parent, false))
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = scheduleList[position]
        holder.render(item, onClickListener)
    }
}