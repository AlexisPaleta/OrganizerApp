package com.fcc.organizador.homework.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcc.organizador.R
import com.fcc.organizador.homework.Homework

class HomeworkAdapter(
    private val homeworkList: List<Homework>,
    private val onClickListener: (Homework) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM   = 1
    }

    /** Lista plana con headers intercalados, reconstruida cuando cambia homeworkList. */
    private var flatList: List<HomeworkListItem> = HomeworkGrouper.build(homeworkList)

    /** Llama esto después de cualquier cambio en homeworkList para reconstruir la lista plana. */
    fun refresh() {
        flatList = HomeworkGrouper.build(homeworkList)
        notifyDataSetChanged()
    }

    // -------------------------------------------------------------------------
    // Mapeo entre posición flat ↔ posición real en homeworkList
    // -------------------------------------------------------------------------

    /** Devuelve el [Homework] en la posición flat dada, o null si es un header. */
    fun getHomeworkAt(flatPosition: Int): Homework? =
        (flatList.getOrNull(flatPosition) as? HomeworkListItem.Item)?.homework

    /**
     * Devuelve la posición flat del [Homework] con el id dado.
     * Útil para notificar cambios puntuales sin reconstruir toda la lista.
     */
    fun flatPositionOf(homework: Homework): Int =
        flatList.indexOfFirst { it is HomeworkListItem.Item && it.homework.id == homework.id }

    // -------------------------------------------------------------------------
    // Adapter overrides
    // -------------------------------------------------------------------------

    override fun getItemViewType(position: Int): Int =
        when (flatList[position]) {
            is HomeworkListItem.Header -> VIEW_TYPE_HEADER
            is HomeworkListItem.Item   -> VIEW_TYPE_ITEM
        }

    override fun getItemCount(): Int = flatList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(R.layout.item_homework_header, parent, false)
            )
            else -> HomeworkViewHolder(
                inflater.inflate(R.layout.item_homework, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = flatList[position]) {
            is HomeworkListItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is HomeworkListItem.Item   -> (holder as HomeworkViewHolder).render(item.homework, onClickListener)
        }
    }

    // -------------------------------------------------------------------------
    // Header ViewHolder (inline, simple)
    // -------------------------------------------------------------------------

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvHeader: TextView = view.findViewById(R.id.tvSectionHeader)
        fun bind(title: String) { tvHeader.text = title }
    }
}