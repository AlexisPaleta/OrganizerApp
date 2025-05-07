package com.fcc.organizador.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.fcc.organizador.Teacher
import com.fcc.organizador.databinding.ItemTeacherBinding

class TeacherViewHolder(view: View): ViewHolder(view) {

    val binding = ItemTeacherBinding.bind(view)

    fun render(teacher: Teacher, onClickListener: (Teacher) -> Unit, onClickDelete: (Int) -> Unit){
        binding.teacherName.text = teacher.name
        binding.teacherCubicle.text = teacher.cubicle
        binding.teacherContact.text = teacher.contact
        binding.teacherDescription.text = teacher.description

        binding.teacherCardItem.setOnClickListener { onClickListener(teacher) }
        binding.teacherDeleteButton.setOnClickListener { onClickDelete(adapterPosition) }
    }

}