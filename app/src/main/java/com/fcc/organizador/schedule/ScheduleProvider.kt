package com.fcc.organizador.schedule

import android.graphics.Color

class ScheduleProvider {

    companion object{
        val scheduleList = listOf(
            Schedule("Horario", Color.YELLOW), Schedule("Lunes", Color.YELLOW), Schedule("Martes", Color.YELLOW),
            Schedule("09:00 - 10:00", Color.YELLOW), Schedule("Mate", Color.YELLOW), Schedule("Ingles", Color.YELLOW)
        )
    }
}