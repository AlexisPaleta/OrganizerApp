package com.fcc.organizador.homework.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fcc.organizador.databinding.ItemHomeworkBinding
import com.fcc.organizador.homework.Homework
import java.util.Calendar
import kotlin.math.min

class HomeworkViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHomeworkBinding.bind(view)
    val foregroundView = binding.foregroundView
    val editBackground  = binding.editBackground
    val deleteBackground = binding.deleteBackground

    fun render(homework: Homework, onClickListener: (Homework) -> Unit) {
        binding.homeworkTitle.text    = homework.title
        binding.homeworkDueTime.text  = "Fecha programada: ${homework.dateText}"
        binding.homeworkTimeText.text = "Hora programada: ${homework.timeText}"

        foregroundView.setBackgroundColor(colorForState(homework))

        binding.homeworkCardItem.setOnClickListener { onClickListener(homework) }
        resetSwipePosition()
    }

    fun resetSwipePosition() {
        foregroundView.translationX  = 0f
        editBackground.visibility   = View.GONE
        deleteBackground.visibility = View.GONE
    }

    companion object {

        // ── Colores por estado ────────────────────────────────────────────────
        private const val COLOR_OVERDUE    = "#C0392B" // rojo    — vencida
        private const val COLOR_TODAY      = "#439A86" // verde — vence hoy (verde original)
        private const val COLOR_TOMORROW   = "#E67E22" // naranja    — vence mañana
        private const val COLOR_UPCOMING   = "#9EA302" // amarillo   — próximamente
        private const val COLOR_COMPLETED  = "#034f9c" // azul    — completada

        fun colorForState(homework: Homework): Int {
            if (homework.statusCompleted) return Color.parseColor(COLOR_COMPLETED)

            val now           = Calendar.getInstance()
            val todayStart    = now.atStartOfDay()
            val todayEnd      = now.atEndOfDay()
            val tomorrowEnd   = (now.clone() as Calendar)
                .also { it.add(Calendar.DAY_OF_YEAR, 1) }.atEndOfDay()

            return when {
                homework.dueDateMillis < todayStart  -> Color.parseColor(COLOR_OVERDUE)
                homework.dueDateMillis <= todayEnd   -> Color.parseColor(COLOR_TODAY)
                homework.dueDateMillis <= tomorrowEnd -> Color.parseColor(COLOR_TOMORROW)
                else                                  -> Color.parseColor(COLOR_UPCOMING)
            }
        }

        private fun Calendar.atStartOfDay(): Long =
            (clone() as Calendar).also {
                it.set(Calendar.HOUR_OF_DAY, 0); it.set(Calendar.MINUTE, 0)
                it.set(Calendar.SECOND, 0);      it.set(Calendar.MILLISECOND, 0)
            }.timeInMillis

        private fun Calendar.atEndOfDay(): Long =
            (clone() as Calendar).also {
                it.set(Calendar.HOUR_OF_DAY, 23); it.set(Calendar.MINUTE, 59)
                it.set(Calendar.SECOND, 59);       it.set(Calendar.MILLISECOND, 999)
            }.timeInMillis

        // ── Swipe ─────────────────────────────────────────────────────
        fun handleSwipe(holder: HomeworkViewHolder, dX: Float) {
            when {
                dX > 0 -> { // swiping right → edit
                    holder.deleteBackground.visibility = View.GONE
                    holder.editBackground.visibility   = View.VISIBLE
                    holder.editBackground.alpha = min(1f, dX / holder.itemView.width * 2)
                }
                dX < 0 -> { // swiping left → delete
                    holder.editBackground.visibility   = View.GONE
                    holder.deleteBackground.visibility = View.VISIBLE
                    holder.deleteBackground.alpha = min(1f, -dX / holder.itemView.width * 2)
                }
            }
            holder.foregroundView.translationX = dX
        }
    }
}