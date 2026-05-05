package com.fcc.organizador.homework.adapter

import com.fcc.organizador.homework.Homework

/** Lista plana que mezcla headers y tareas para el RecyclerView. */
sealed class HomeworkListItem {
    data class Header(val title: String) : HomeworkListItem()
    data class Item(val homework: Homework) : HomeworkListItem()
}
