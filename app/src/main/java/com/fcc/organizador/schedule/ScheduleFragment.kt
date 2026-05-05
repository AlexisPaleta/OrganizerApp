package com.fcc.organizador.schedule

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fcc.organizador.R
import com.fcc.organizador.databinding.FragmentScheduleBinding
import com.fcc.organizador.db.AppDatabaseHelper
import com.fcc.organizador.schedule.adapter.ScheduleAdapter
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.colorpickerview.ColorPickerDialog

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScheduleFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var scheduleMutableList: MutableList<Schedule>
    private lateinit var adapter: ScheduleAdapter
    private lateinit var glmanager: LinearLayoutManager
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var db: AppDatabaseHelper

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
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabaseHelper(requireContext())
        scheduleMutableList = db.getAllScheduleCells()

        glmanager = GridLayoutManager(requireContext(), scheduleViewModel.getColumnsCount())
        initRecyclerView()

        binding.btnAddRow.setOnClickListener { addRowSection() }
        binding.btnDeleteRow.setOnClickListener { deleteRowSection() }
        binding.btnExportPdf.setOnClickListener { exportToPdf() }
    }

    private fun initRecyclerView() {
        adapter = ScheduleAdapter(
            scheduleList = scheduleMutableList,
            onClickListener = { schedule -> onItemSelected(schedule) }
        )
        binding.recyclerSchedule.layoutManager = glmanager
        binding.recyclerSchedule.adapter = adapter
    }

    private fun addRowSection() {
        val columnsCount = scheduleViewModel.getColumnsCount()
        val startPosition = scheduleMutableList.size
        var newPosition = startPosition

        for (a in 1..columnsCount) {
            val cellSchedule = Schedule("Presiona para editar", Color.argb(255, 249, 231, 151), newPosition)
            scheduleMutableList.add(cellSchedule)
            db.insertScheduleCell(cellSchedule)
            newPosition++
        }
        adapter.notifyItemRangeInserted(startPosition, columnsCount)
    }

    private fun deleteRowSection() {
        val columnsCount = scheduleViewModel.getColumnsCount()

        if (scheduleMutableList.size <= columnsCount) {
            Toast.makeText(requireContext(), "No se puede eliminar la primera fila", Toast.LENGTH_SHORT).show()
            return
        }

        val rangePositions = scheduleMutableList.size - columnsCount
        for (a in 1..columnsCount) {
            val removingPosition = scheduleMutableList.size - 1
            scheduleMutableList.removeAt(removingPosition)
            db.deleteScheduleCell(removingPosition)
        }
        adapter.notifyItemRangeRemoved(rangePositions, columnsCount)
    }

    private fun exportToPdf() {
        if (scheduleMutableList.isEmpty()) {
            Toast.makeText(requireContext(), "El horario está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfFile = SchedulePdfExporter.export(
            context = requireContext(),
            scheduleList = scheduleMutableList,
            columnsCount = scheduleViewModel.getColumnsCount()
        )

        if (pdfFile == null) {
            Toast.makeText(requireContext(), "Error al generar el PDF", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "PDF generado correctamente", Toast.LENGTH_SHORT).show()

        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                pdfFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            startActivity(Intent.createChooser(intent, "Abrir PDF con..."))
        } catch (_: Exception) {
            Toast.makeText(
                requireContext(),
                "No se encontró una app para abrir PDF. Archivo guardado en caché.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onItemSelected(schedule: Schedule) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_config_schedule, null)
        val editText = dialogView.findViewById<EditText>(R.id.editTextActivity)
        val viewColor = dialogView.findViewById<View>(R.id.viewColorPreview)
        val btnPickColor = dialogView.findViewById<Button>(R.id.btnPickColor)
        val btnAccept = dialogView.findViewById<Button>(R.id.btnAccept)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnClose)

        var selectedColor: Int = schedule.color.asOpaqueColor()

        editText.setText(
            if (schedule.content == "Presiona para editar") "" else schedule.content
        )
        viewColor.setBackgroundColor(selectedColor)

        btnPickColor.setOnClickListener {
            val builder = ColorPickerDialog.Builder(requireContext())
                .setTitle("Selecciona un color")
                .setPositiveButton("Aceptar", ColorEnvelopeListener { envelope, _ ->
                    selectedColor = envelope.color.asOpaqueColor()
                    viewColor.setBackgroundColor(selectedColor)
                })
                .setNegativeButton("Cancelar") { dialogInterface, _ -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true)

            // Pre-seleccionar el color actual de la celda
            builder.getColorPickerView().setInitialColor(selectedColor)

            builder.show()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnAccept.setOnClickListener {
            var content = editText.text.toString()
            if (content.isEmpty()) content = "Presiona para editar"
            schedule.content = content
            schedule.color = selectedColor.asOpaqueColor()
            db.updateScheduleCell(Schedule(content, schedule.color, schedule.position))
            adapter.notifyItemChanged(schedule.position)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun Int.asOpaqueColor(): Int = this or 0xFF000000.toInt()
}