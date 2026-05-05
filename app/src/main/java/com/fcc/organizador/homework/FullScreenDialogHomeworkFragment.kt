package com.fcc.organizador.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.fcc.organizador.R
import com.fcc.organizador.databinding.DialogHomeworkBinding
import com.fcc.organizador.db.AppDatabaseHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

class FullScreenDialogHomeworkFragment : DialogFragment() {

    private var _binding: DialogHomeworkBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeworkViewModel: HomeworkViewModel
    private lateinit var db: AppDatabaseHelper

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

        db = AppDatabaseHelper(requireContext())

        if (homeworkViewModel.getEditing()) {
            fillOutHomeworkInformation()
            binding.dialogTitle.text = "Editar Tarea"
        } else {
            binding.dialogTitle.text = "Agregar Nueva Tarea"
        }

        binding.btnSave.setOnClickListener { saveHomeworkInfo(homeworkViewModel.getEditing()) }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.textViewDate.setOnClickListener { showDatePicker() }

        binding.textViewTime.setOnClickListener {
            if (selectedYear == 0) {
                Toast.makeText(context, "Primero selecciona una fecha", Toast.LENGTH_SHORT).show()
            } else {
                showTimePicker()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun fillOutHomeworkInformation() {
        val homework = homeworkViewModel.getEditingHomework() ?: return
        binding.editTextTitle.setText(homework.title)
        binding.editTextDescription.setText(homework.description)

        if (homework.dueDateMillis > 0) {
            val calendar = Calendar.getInstance().apply { timeInMillis = homework.dueDateMillis }
            selectedYear   = calendar.get(Calendar.YEAR)
            selectedMonth  = calendar.get(Calendar.MONTH)
            selectedDay    = calendar.get(Calendar.DAY_OF_MONTH)
            selectedHour   = calendar.get(Calendar.HOUR_OF_DAY)
            selectedMinute = calendar.get(Calendar.MINUTE)
            dueTimeMillis  = homework.dueDateMillis
            updateDateButton()
            updateTimeButton()
            updateSummary()
        }
    }

    private fun saveHomeworkInfo(editing: Boolean) {
        val title       = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()

        if (!validation(title, editing)) return

        val dateText = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        val timeText = "%d:%02d".format(selectedHour, selectedMinute)

        val idValue = if (editing && homeworkViewModel.getEditingHomework() != null)
            homeworkViewModel.getEditingHomework()!!.id else 0
        val completedState = if (editing && homeworkViewModel.getEditingHomework() != null)
            homeworkViewModel.getEditingHomework()!!.statusCompleted else false

        val homework = Homework(idValue, title, description, dueTimeMillis, dateText, timeText, completedState)

        if (!editing) homeworkViewModel.setNewHomework(homework)
        else homeworkViewModel.setEditHomework(homework)

        dismiss()
    }

    private fun validation(title: String, editing: Boolean): Boolean {
        var validated = true

        if (title.isEmpty()) {
            binding.titleLayout.error = "Ingresa un título"
            validated = false
        } else if (!editing && db.homeworkTitleExists(title)) {
            binding.titleLayout.error = "El titulo ya fue registrado"
            validated = false
        } else {
            binding.titleLayout.error = null
        }

        if (selectedYear == 0) {
            binding.errorDate.text = "Ingresa una fecha"
            binding.errorDate.visibility = View.VISIBLE
            validated = false
        } else {
            binding.errorDate.visibility = View.GONE
        }

        if (dueTimeMillis == 0L || selectedHour == 0 && selectedMinute == 0 && selectedYear == 0) {
            // Solo mostrar error de hora si la fecha ya fue seleccionada
            if (selectedYear != 0 && (binding.textViewTime.text == "Hora")) {
                binding.errorTime.text = "Ingresa una hora"
                binding.errorTime.visibility = View.VISIBLE
                validated = false
            } else if (selectedYear == 0) {
                // sin fecha tampoco hay hora
            }
        } else {
            binding.errorTime.visibility = View.GONE
        }

        return validated
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar fecha")
            .setSelection(
                if (dueTimeMillis > 0L) dueTimeMillis
                else MaterialDatePicker.todayInUtcMilliseconds()
            )
            .build()

        picker.addOnPositiveButtonClickListener { selectionMillis ->
            val utc = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = selectionMillis
            }
            selectedYear  = utc.get(Calendar.YEAR)
            selectedMonth = utc.get(Calendar.MONTH)
            selectedDay   = utc.get(Calendar.DAY_OF_MONTH)

            binding.errorDate.visibility = View.GONE
            updateDateButton()
            showTimePicker()
        }

        picker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTitleText("Seleccionar hora")
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(if (selectedHour > 0) selectedHour else Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(if (selectedMinute > 0) selectedMinute else Calendar.getInstance().get(Calendar.MINUTE))
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()

        picker.addOnPositiveButtonClickListener {
            selectedHour   = picker.hour
            selectedMinute = picker.minute

            val cal = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0)
                set(Calendar.MILLISECOND, 0)
            }
            dueTimeMillis = cal.timeInMillis

            binding.errorTime.visibility = View.GONE
            updateTimeButton()
            updateSummary()
        }

        picker.show(parentFragmentManager, "TIME_PICKER")
    }

    /** Actualiza el texto del botón de fecha con el valor seleccionado. */
    private fun updateDateButton() {
        binding.textViewDate.text = "%d/%d/%d".format(selectedDay, selectedMonth + 1, selectedYear)
    }

    /** Actualiza el texto del botón de hora con el valor seleccionado. */
    private fun updateTimeButton() {
        binding.textViewTime.text = "%d:%02d".format(selectedHour, selectedMinute)
    }

    /** Muestra el resumen compacto debajo de los botones. */
    private fun updateSummary() {
        if (dueTimeMillis == 0L) return
        val days = arrayOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        val cal  = Calendar.getInstance().apply { timeInMillis = dueTimeMillis }
        val dow  = days[cal.get(Calendar.DAY_OF_WEEK) - 1]
        val ampm = if (selectedHour < 12) "AM" else "PM"
        val h12  = when {
            selectedHour == 0  -> 12
            selectedHour > 12  -> selectedHour - 12
            else               -> selectedHour
        }
        binding.textDateTimeSummary.text =
            "$dow $selectedDay/${selectedMonth + 1}/$selectedYear  •  %d:%02d %s".format(h12, selectedMinute, ampm)
        binding.layoutDateTimeSummary.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = FullScreenDialogHomeworkFragment()
    }
}