package com.fcc.organizador.schedule

import android.graphics.Color

class ScheduleProvider {

    companion object{
        val scheduleList = listOf(
            Schedule("Horario", Color.YELLOW, 0), Schedule("Lunes", Color.YELLOW, 1), Schedule("Martes", Color.YELLOW, 2),
            Schedule("Miércoles", Color.YELLOW, 3), Schedule("Jueves", Color.YELLOW, 4), Schedule("Viernes", Color.YELLOW, 5),
            Schedule("Sábado", Color.YELLOW, 6), Schedule("09:00 - 10:00", Color.YELLOW, 7), Schedule("Mate", Color.YELLOW, 8),
            Schedule("Ingles", Color.YELLOW, 9)
        )
    }
}