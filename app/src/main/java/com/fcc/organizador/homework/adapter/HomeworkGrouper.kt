package com.fcc.organizador.homework.adapter

import com.fcc.organizador.homework.Homework
import java.util.Calendar

/**
 * Agrupa una lista de [Homework] en secciones:
 * Vencidas · Hoy · Mañana · Próximamente · Completadas
 */
object HomeworkGrouper {

    fun build(list: List<Homework>): List<HomeworkListItem> {
        val now     = Calendar.getInstance()
        val todayStart  = now.startOfDay()
        val todayEnd    = now.endOfDay()
        val tomorrowStart = (now.clone() as Calendar).also { it.add(Calendar.DAY_OF_YEAR, 1) }.startOfDay()
        val tomorrowEnd   = (now.clone() as Calendar).also { it.add(Calendar.DAY_OF_YEAR, 1) }.endOfDay()

        val overdue     = mutableListOf<Homework>()
        val today       = mutableListOf<Homework>()
        val tomorrow    = mutableListOf<Homework>()
        val upcoming    = mutableListOf<Homework>()
        val completed   = mutableListOf<Homework>()

        list.forEach { hw ->
            when {
                hw.statusCompleted -> completed.add(hw)
                hw.dueDateMillis < todayStart -> overdue.add(hw)
                hw.dueDateMillis <= todayEnd  -> today.add(hw)
                hw.dueDateMillis <= tomorrowEnd -> tomorrow.add(hw)
                else -> upcoming.add(hw)
            }
        }

        // Ordenar cada grupo por fecha ascendente
        val sort: (Homework) -> Long = { it.dueDateMillis }
        overdue.sortBy(sort)
        today.sortBy(sort)
        tomorrow.sortBy(sort)
        upcoming.sortBy(sort)
        completed.sortBy(sort)

        val result = mutableListOf<HomeworkListItem>()

        fun addSection(label: String, items: List<Homework>) {
            if (items.isEmpty()) return
            result.add(HomeworkListItem.Header(label))
            items.forEach { result.add(HomeworkListItem.Item(it)) }
        }

        addSection("Vencidas", overdue)
        addSection("Hoy", today)
        addSection("Mañana", tomorrow)
        addSection("Próximamente", upcoming)
        addSection("Completadas", completed)

        return result
    }

    private fun Calendar.startOfDay(): Long =
        (clone() as Calendar).also {
            it.set(Calendar.HOUR_OF_DAY, 0); it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0);      it.set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun Calendar.endOfDay(): Long =
        (clone() as Calendar).also {
            it.set(Calendar.HOUR_OF_DAY, 23); it.set(Calendar.MINUTE, 59)
            it.set(Calendar.SECOND, 59);       it.set(Calendar.MILLISECOND, 999)
        }.timeInMillis
}
