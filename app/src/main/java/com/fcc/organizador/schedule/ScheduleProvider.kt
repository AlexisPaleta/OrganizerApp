package com.fcc.organizador.schedule

import android.graphics.Color

class ScheduleProvider {

    companion object{
        val scheduleList = listOf(
            Schedule("Horario",  Color.parseColor("#68f9a7"), 0), Schedule("Lunes", Color.YELLOW, 1), Schedule("Martes", Color.parseColor("#f9ffa3"), 2),
            Schedule("Miércoles", Color.parseColor("#F44336"), 3), Schedule("Jueves", Color.parseColor("#FFEB3B"), 4), Schedule("Viernes", Color.WHITE, 5),
            Schedule("Sábado", Color.parseColor("#4CAF50"), 6), Schedule("09:00 - 10:00", Color.parseColor("#00d6db"), 7), Schedule("Mate", Color.YELLOW, 8),
            Schedule("Ingles", Color.parseColor("#f9ffa3"), 9), Schedule("Historia", Color.parseColor("#F44336"), 10), Schedule("Química", Color.parseColor("#FFEB3B"), 11),
            Schedule("Recreo", Color.WHITE, 12), Schedule("Libre", Color.parseColor("#4CAF50"), 13)
        )
    }
}