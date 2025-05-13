package com.fcc.organizador.homework

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.fcc.organizador.R
import com.fcc.organizador.databinding.DialogHomeworkBinding
import java.util.Calendar

class FullScreenDialogHomeworkFragment: DialogFragment() {
    private var _binding: DialogHomeworkBinding? = null

    private val binding get() = _binding!!
    private lateinit var homeworkViewModel: HomeworkViewModel

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0
    private var selectedHour = 0
    private var selectedMinute = 0
    private var dueTimeMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeworkViewModel = ViewModelProvider(requireActivity()).get(HomeworkViewModel::class.java)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogHomeworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (homeworkViewModel.getEditing()){ //If the dialog was called by the edit swipe option
            fillOutHomeworkInformation()
        }

        binding.btnSave.setOnClickListener {
            saveHomeworkInfo(homeworkViewModel.getEditing())
        }

        binding.textViewDate.setOnClickListener {
            showDatePicker()
        }

        binding.textViewTime.setOnClickListener {
            if (dueTimeMillis == 0L) {
                Toast.makeText(context, "Primero selecciona una fecha", Toast.LENGTH_SHORT).show()
            } else {
                showTimePicker()
            }
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun fillOutHomeworkInformation() {
        val homework = homeworkViewModel.getEditingHomework()
        if (homework != null) {
            binding.editTextTitle.setText(homework.title)
            binding.editTextDescription.setText(homework.description)

            if (homework.dueDateMillis > 0) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = homework.dueDateMillis
                }

                selectedYear = calendar.get(Calendar.YEAR)
                selectedMonth = calendar.get(Calendar.MONTH)
                selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
                selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
                selectedMinute = calendar.get(Calendar.MINUTE)

                val dateText = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                val timeText = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"

                binding.textViewDate.text = dateText
                binding.textViewTime.text = timeText

                dueTimeMillis = homework.dueDateMillis
            }
        }
    }

    private fun saveHomeworkInfo(editing: Boolean) {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.editTextTitle.error = "Ingresa un título"
            return
        }

        val homework = Homework(title, description, dueTimeMillis)

        if (!editing) {
            homeworkViewModel.setNewHomework(homework)
        } else {
            homeworkViewModel.setEditHomework(homework)
        }


        dismiss()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedYear = calendar.get(Calendar.YEAR)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedYear = year
                selectedMonth = month
                selectedDay = day
                binding.textViewDate.text = "$day/${month + 1}/$year"
                showTimePicker()
            },
            selectedYear,
            selectedMonth,
            selectedDay
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
        selectedMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                binding.textViewTime.text = "$hour:$minute"

                // Guardar fecha completa en millis
                val calendarSelected = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay, hour, minute, 0)
                }
                dueTimeMillis = calendarSelected.timeInMillis
            },
            selectedHour,
            selectedMinute,
            false
        )
        timePickerDialog.show()
    }

    companion object {
        fun newInstance() = FullScreenDialogHomeworkFragment()
    }


}