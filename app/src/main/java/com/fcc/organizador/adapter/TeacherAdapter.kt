package com.fcc.organizador.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcc.organizador.R
import com.fcc.organizador.Teacher

class TeacherAdapter(private val teacherList: List<Teacher>): RecyclerView.Adapter<TeacherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TeacherViewHolder(layoutInflater.inflate(R.layout.item_teacher, parent, false))
    }

    override fun getItemCount(): Int {
        return teacherList.size
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val item = teacherList[position]
        holder.render(item)
    }


}