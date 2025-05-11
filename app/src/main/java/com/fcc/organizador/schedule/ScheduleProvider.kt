package com.fcc.organizador.schedule

import android.graphics.Color

class ScheduleProvider {

    companion object{
        val scheduleList = listOf(
            Schedule("Horario",  Color.parseColor("#68f9a7"), 0), Schedule("Lunes", Color.YELLOW, 1), Schedule("Martes", Color.parseColor("#f9ffa3"), 2),
            Schedule("Miércoles", Color.parseColor("#F44336"), 3), Schedule("Jueves", Color.parseColor("#FFEB3B"), 4), Schedule("Viernes", Color.WHITE, 5),
            Schedule("Sábado", Color.parseColor("#4CAF50"), 6)
        )
    }
}