package com.fcc.organizador.homework

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcc.organizador.databinding.DialogHomeworkSelectedBinding
import com.fcc.organizador.databinding.FragmentHomeworkBinding
import com.fcc.organizador.db.AppDatabaseHelper
import com.fcc.organizador.homework.adapter.HomeworkAdapter
import com.fcc.organizador.homework.adapter.HomeworkViewHolder
import com.fcc.organizador.homework.notification.cancelNotification
import com.fcc.organizador.homework.notification.scheduleExactNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeworkFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentHomeworkBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeworkMutableList: MutableList<Homework>
    private lateinit var adapter: HomeworkAdapter
    private lateinit var llmanager: LinearLayoutManager
    private lateinit var homeworkViewModel: HomeworkViewModel
    private lateinit var db: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        homeworkViewModel = ViewModelProvider(requireActivity())[HomeworkViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabaseHelper(requireContext())
        homeworkMutableList = db.getAllHomework()

        homeworkViewModel.getNewHomework().observe(viewLifecycleOwner, Observer { homework ->
            if (homework != null && !homeworkViewModel.getEditing()) {
                addHomework(homework)
                homeworkViewModel.homeworkAdded()
            }
        })

        homeworkViewModel.getEditHomework().observe(viewLifecycleOwner, Observer { homework ->
            if (homework != null && homeworkViewModel.getEditing()) {
                editedHomework(homework)
                homeworkViewModel.homeworkEdited()
            }
        })

        binding.addHomeworkFloatingButton.setOnClickListener { createHomework() }

        llmanager = LinearLayoutManager(requireContext())
        initRecyclerView()
        attachSwipeHelper()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // -------------------------------------------------------------------------
    // RecyclerView setup
    // -------------------------------------------------------------------------

    private fun initRecyclerView() {
        adapter = HomeworkAdapter(
            homeworkList = homeworkMutableList,
            onClickListener = { homework -> onItemSelected(homework) }
        )
        checkIfEmpty()
        binding.recyclerHomework.layoutManager = llmanager
        binding.recyclerHomework.adapter = adapter
        // Sin DividerItemDecoration: los headers ya separan visualmente las secciones
    }

    private fun attachSwipeHelper() {
        val callback = object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Los headers NO son swipeables
                if (viewHolder is HomeworkAdapter.HeaderViewHolder) return 0
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val flatPos = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT  -> deleteFunction(flatPos)
                    ItemTouchHelper.RIGHT -> editFunction(flatPos)
                }
            }

            override fun onChildDraw(
                canvas: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE &&
                    viewHolder is HomeworkViewHolder) {
                    HomeworkViewHolder.handleSwipe(viewHolder, dX)
                } else {
                    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.translationX = 0f
                if (viewHolder is HomeworkViewHolder) {
                    viewHolder.editBackground.visibility = View.GONE
                    viewHolder.deleteBackground.visibility = View.GONE
                }
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerHomework)
    }

    // -------------------------------------------------------------------------
    // CRUD helpers
    // -------------------------------------------------------------------------

    private fun checkIfEmpty() {
        if (homeworkMutableList.isEmpty()) {
            binding.recyclerHomework.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.recyclerHomework.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }

    private fun createHomework() {
        homeworkViewModel.setEditing(false)
        homeworkViewModel.setHomeworkListLastPosition(homeworkMutableList.size - 1)
        FullScreenDialogHomeworkFragment.newInstance()
            .show(parentFragmentManager, "AddHomeworkDialog")
    }

    private fun addHomework(homework: Homework) {
        val id = db.insertHomework(homework)
        homework.id = id
        homeworkMutableList.add(homework)
        checkIfEmpty()
        // Reconstruir lista plana con el nuevo item y notificar
        adapter.refresh()
        scheduleExactNotification(requireContext(), homework)
    }

    private fun editedHomework(homework: Homework) {
        val position = homeworkViewModel.getEditedPosition()
        homeworkMutableList[position] = homework
        db.updateHomework(homework)
        cancelNotification(requireContext(), homework.id)
        if (!homework.statusCompleted) scheduleExactNotification(requireContext(), homework)
        // Reconstruir secciones por si cambió de grupo
        adapter.refresh()
        val newFlat = adapter.flatPositionOf(homework)
        if (newFlat >= 0) llmanager.scrollToPositionWithOffset(newFlat, 10)
    }

    private fun restoreHomework(position: Int, homework: Homework) {
        homeworkMutableList.add(position, homework)
        val id = db.insertHomework(homework)
        homework.id = id
        checkIfEmpty()
        adapter.refresh()
        binding.recyclerHomework.post {
            val flatPos = adapter.flatPositionOf(homework)
            if (flatPos >= 0) {
                val holder = binding.recyclerHomework.findViewHolderForAdapterPosition(flatPos)
                (holder as? HomeworkViewHolder)?.resetSwipePosition()
                llmanager.scrollToPositionWithOffset(flatPos, 10)
            }
        }
        if (!homework.statusCompleted) scheduleExactNotification(requireContext(), homework)
        else cancelNotification(requireContext(), homework.id)
    }

    private fun deleteFunction(flatPosition: Int) {
        val homework = adapter.getHomeworkAt(flatPosition) ?: return
        val listPos = homeworkMutableList.indexOf(homework)
        cancelNotification(requireContext(), homework.id)
        homeworkMutableList.removeAt(listPos)
        db.deleteHomework(homework.id)
        checkIfEmpty()
        adapter.refresh()

        Snackbar.make(binding.root, "Tarea eliminada", Snackbar.LENGTH_LONG)
            .setAction("Deshacer") { restoreHomework(listPos, homework) }
            .setActionTextColor(Color.YELLOW)
            .show()
    }

    private fun editFunction(flatPosition: Int) {
        val homework = adapter.getHomeworkAt(flatPosition) ?: return
        val listPos = homeworkMutableList.indexOf(homework)
        adapter.refresh() // resetea visual del swipe
        binding.recyclerHomework.post {
            val holder = binding.recyclerHomework.findViewHolderForAdapterPosition(flatPosition)
            (holder as? HomeworkViewHolder)?.resetSwipePosition()
        }
        homeworkViewModel.setEditing(true)
        homeworkViewModel.setEditingHomework(homework)
        homeworkViewModel.setEditedPosition(listPos)
        FullScreenDialogHomeworkFragment.newInstance()
            .show(parentFragmentManager, "EditHomeworkDialog")
    }

    // -------------------------------------------------------------------------
    // Detail dialog
    // -------------------------------------------------------------------------

    private fun onItemSelected(homework: Homework) {
        val dialogBinding = DialogHomeworkSelectedBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        with(dialogBinding) {
            textViewHomeworkTitle.text = homework.title
            textViewHomeworkDateText.text = homework.dateText
            textViewHomeworkTimeText.text = homework.timeText
            textViewHomeworkDescription.text = homework.description

            if (homework.statusCompleted) {
                toggleCompleted.isEnabled = false
                toggleCompleted.text = "Tarea Completada"
            }

            toggleCompleted.setOnClickListener {
                homework.statusCompleted = true
                db.updateHomework(homework)
                cancelNotification(requireContext(), homework.id)
                // Mover a sección Completadas
                adapter.refresh()
                toggleCompleted.isEnabled = false
                toggleCompleted.text = "Tarea Completada"
                Toast.makeText(requireContext(), "Tarea marcada como completada", Toast.LENGTH_SHORT).show()
            }

            btnClose.setOnClickListener { dialog.dismiss() }
        }

        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeworkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}