package com.fcc.organizador

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fcc.organizador.adapter.TeacherAdapter
import com.fcc.organizador.databinding.FragmentTeachersBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TeachersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TeachersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentTeachersBinding? = null
    private val binding get() = _binding!!
    private var teacherMutableList: MutableList<Teacher> = TeacherProvider.teachersList.toMutableList()
    private lateinit var adapter: TeacherAdapter
    private lateinit var llmanager: LinearLayoutManager
    private lateinit var teacherViewModel: TeacherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        teacherViewModel = ViewModelProvider(requireActivity()).get(TeacherViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val teacherAddedObserver = Observer<Teacher?>{ teacher ->
            if (teacher!=null){
                addTeacher(teacher)
                teacherViewModel.teacherAdded()
            }
        }

        teacherViewModel.getTeacher().observe(viewLifecycleOwner, teacherAddedObserver)

        binding.addTeacherFloatingButton.setOnClickListener { createTeacher() }
        llmanager = LinearLayoutManager(requireContext())
        initRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTeachersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(){
        adapter = TeacherAdapter(
            teacherList = teacherMutableList,
            onClickListener = { teacher -> onItemSelected(teacher) },
            onClickDelete = { position -> onDeletedItem(position) }
        )

        val decoration = DividerItemDecoration(requireContext(), llmanager.orientation)
        val recyclerView = binding.recyclerTeachers

        recyclerView.layoutManager = llmanager
        recyclerView.adapter = adapter
        binding.recyclerTeachers.addItemDecoration(decoration)
    }

    private fun onItemSelected(teacher: Teacher){
        Toast.makeText(requireContext(), teacher.name, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int){
        teacherMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun createTeacher() {
        val dialog = FullScreenDialogTeacherFragment.newInstance()
        dialog.show(parentFragmentManager, "AddTeacherDialog")
    }

    private fun addTeacher(teacher: Teacher) {
        teacherMutableList.add(teacher)
        adapter.notifyItemInserted(teacherMutableList.size - 1)
        llmanager.scrollToPositionWithOffset(teacherMutableList.size - 1, 10)

        Toast.makeText(requireContext(), "${teacher.name} agregado", Toast.LENGTH_SHORT).show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TeachersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TeachersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}