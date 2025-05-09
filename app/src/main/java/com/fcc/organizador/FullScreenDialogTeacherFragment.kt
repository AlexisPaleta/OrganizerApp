package com.fcc.organizador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.fcc.organizador.databinding.DialogTeacherBinding

class FullScreenDialogTeacherFragment: DialogFragment() {
    private var _binding: DialogTeacherBinding? = null
    private val binding get() = _binding!!
    private lateinit var teacherViewModel: TeacherViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacherViewModel = ViewModelProvider(requireActivity()).get(TeacherViewModel::class.java)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (teacherViewModel.getEditing()){ //If the dialog was called by the edit swipe option
            fillOutTeacherInformation()
        }

        binding.btnSave.setOnClickListener {
            saveTeacherInfo(teacherViewModel.getEditing())
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun addTeacherLogic(){
        val name = binding.editTextName.text.toString().trim()
        val cubicle = binding.editTextCubicle.text.toString().trim()
        val email = binding.editTextMail.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()

        if (validateInputs(name, cubicle, email, description)) {
            val newTeacher = Teacher(name, cubicle, email, description)
            teacherViewModel.setNewTeacher(newTeacher)
            dismiss()
        } else {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fillOutTeacherInformation(){
        val teacher = teacherViewModel.getEditingTeacher()
        if (teacher != null) {
            binding.editTextName.setText(teacher.name)
            binding.editTextCubicle.setText(teacher.cubicle)
            binding.editTextMail.setText(teacher.contact)
            binding.editTextDescription.setText(teacher.description)
        }
    }

    private fun editTeacherLogic(){

    }

    private fun saveTeacherInfo(editing: Boolean){
        val name = binding.editTextName.text.toString().trim()
        val cubicle = binding.editTextCubicle.text.toString().trim()
        val email = binding.editTextMail.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()

        if (validateInputs(name, cubicle, email, description)) {
            val newTeacher = Teacher(name, cubicle, email, description)

            if (!editing){
                teacherViewModel.setNewTeacher(newTeacher)
            }else{
                teacherViewModel.setEditTeacher(newTeacher)
            }

            dismiss()
        } else {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(vararg fields: String): Boolean {
        return fields.all { it.isNotEmpty() }
    }

    companion object {
        fun newInstance() = FullScreenDialogTeacherFragment()
    }
}