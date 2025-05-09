package com.fcc.organizador.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fcc.organizador.databinding.FragmentScheduleBinding
import com.fcc.organizador.schedule.adapter.ScheduleAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        val startPosition = scheduleMutableList.size
        var cellSchedule: Schedule
        for (i in 1..3){
            cellSchedule = Schedule("Click para editar", 1)
            scheduleMutableList.add(cellSchedule)
        }
        adapter.notifyItemRangeInserted(startPosition,3)
    }

    private fun deleteRowSection(){

        if (scheduleMutableList.size <= 3){
            Toast.makeText(requireContext(), "To small list", Toast.LENGTH_SHORT).show()
            return
        }

        val rangePositions = scheduleMutableList.size - 3
        for (i in 1..3){
            scheduleMutableList.removeAt(scheduleMutableList.size - 1)
        }
        adapter.notifyItemRangeRemoved(rangePositions,3)

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