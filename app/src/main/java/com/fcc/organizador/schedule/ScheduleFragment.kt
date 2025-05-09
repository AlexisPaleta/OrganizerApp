package com.fcc.organizador.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fcc.organizador.R
import com.fcc.organizador.databinding.FragmentScheduleBinding
import com.fcc.organizador.schedule.adapter.ScheduleAdapter
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private var scheduleMutableList: MutableList<Schedule> = ScheduleProvider.scheduleList.toMutableList()
    private lateinit var adapter: ScheduleAdapter
    private lateinit var glmanager: LinearLayoutManager
    private lateinit var scheduleViewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        scheduleViewModel = ViewModelProvider(requireActivity())[ScheduleViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        glmanager = GridLayoutManager(requireContext(),4)
        initRecyclerView()
        binding.btnAddRow.setOnClickListener { addRowSection() }
        binding.btnDeleteRow.setOnClickListener { deleteRowSection() }
        binding.btnEditColumn.setOnClickListener { editWeekdays() }
    }

    private fun initRecyclerView(){
        adapter = ScheduleAdapter(
            scheduleList = scheduleMutableList,
            onClickListener = { schedule -> onItemSelected(schedule) }
        )

        val recyclerView = binding.recyclerSchedule

        recyclerView.layoutManager = glmanager
        recyclerView.adapter = adapter
    }

    private fun addRowSection(){
        val columnsCount = scheduleViewModel.getColumnsCount()
        val startPosition = scheduleMutableList.size
        var cellSchedule: Schedule
        for (i in 1..columnsCount){
            cellSchedule = Schedule("Click para editar", 1)
            scheduleMutableList.add(cellSchedule)
        }
        adapter.notifyItemRangeInserted(startPosition, columnsCount)
    }

    private fun deleteRowSection(){
        val columnsCount = scheduleViewModel.getColumnsCount()

        if (scheduleMutableList.size <= columnsCount){
            Toast.makeText(requireContext(), "To small list", Toast.LENGTH_SHORT).show()
            return
        }

        val rangePositions = scheduleMutableList.size - columnsCount
        for (i in 1..columnsCount){
            scheduleMutableList.removeAt(scheduleMutableList.size - 1)
        }
        adapter.notifyItemRangeRemoved(rangePositions, columnsCount)

    }

    private fun editWeekdays(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_week_days, null)

        val cbMonday = dialogView.findViewById<MaterialCheckBox>(R.id.checkboxMonday)
        val cbTuesday = dialogView.findViewById<MaterialCheckBox>(R.id.checkboxTuesday)
        val cbWednesday = dialogView.findViewById<MaterialCheckBox>(R.id.checkboxWednesday)
        val cbThursday = dialogView.findViewById<MaterialCheckBox>(R.id.checkboxThursday)
        val cbFriday = dialogView.findViewById<MaterialCheckBox>(R.id.checkboxFriday)
        val cbSaturday = dialogView.findViewById<MaterialCheckBox>(R.id.checkboxSaturday)
        val cbSunday= dialogView.findViewById<MaterialCheckBox>(R.id.checkboxSunday)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select weekdays")
            .setView(dialogView)
            .setPositiveButton("Accept") { dialog, _ ->
                val selected = mutableListOf<String>()
                if (cbMonday.isChecked) selected.add("Monday")
                if (cbTuesday.isChecked) selected.add("Tuesday")
                if (cbWednesday.isChecked) selected.add("Wednesday")
                if (cbThursday.isChecked) selected.add("Thursday")
                if (cbFriday.isChecked) selected.add("Friday")
                if (cbSaturday.isChecked) selected.add("Saturday")
                if (cbSunday.isChecked) selected.add("Sunday")

                Toast.makeText(requireContext(), "Selected: ${selected.joinToString()}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onItemSelected(schedule: Schedule){
        Toast.makeText(requireContext(), schedule.text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}