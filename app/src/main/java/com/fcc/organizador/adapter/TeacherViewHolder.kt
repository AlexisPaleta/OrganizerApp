package com.fcc.organizador.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.fcc.organizador.R
import com.fcc.organizador.Teacher

class TeacherViewHolder(view: View): ViewHolder(view) {

    val name = view.findViewById<TextView>(R.id.teacherName)
    val cubicle = view.findViewById<TextView>(R.id.teacherCubicle)
    val contact = view.findViewById<TextView>(R.id.teacherContact)
    val description = view.findViewById<TextView>(R.id.teacherDescription)
    val photo = view.findViewById<ImageView>(R.id.teacherImage)

    fun render(teacher: Teacher){
        name.text = teacher.name
        cubicle.text = teacher.cubicle
        contact.text = teacher.contact
        description.text = teacher.description
    }

}