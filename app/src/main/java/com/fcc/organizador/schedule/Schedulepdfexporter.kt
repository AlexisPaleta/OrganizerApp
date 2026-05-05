package com.fcc.organizador.schedule

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

object SchedulePdfExporter {

    /**
     * Genera un PDF del horario y lo guarda en el directorio de caché de la app.
     * @return El [File] generado, o null si ocurrió un error.
     */
    fun export(context: Context, scheduleList: List<Schedule>, columnsCount: Int): File? {
        if (scheduleList.isEmpty()) return null

        // --- Dimensiones base ---
        val cellWidth = 130f
        val cellHeight = 60f
        val pageWidth = (cellWidth * columnsCount).toInt()
        val rowCount = scheduleList.size / columnsCount
        val pageHeight = (cellHeight * rowCount + 80).toInt() // 80 de margen superior para título

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // --- Paints ---
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 13f
            isAntiAlias = true
        }
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val borderPaint = Paint().apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        val fillPaint = Paint().apply {
            style = Paint.Style.FILL
        }

        // --- Título ---
        canvas.drawText("Horario", 16f, 50f, titlePaint)

        // --- Dibujar celdas ---
        scheduleList.forEachIndexed { index, schedule ->
            val col = index % columnsCount
            val row = index / columnsCount

            val left = col * cellWidth
            val top = 70f + row * cellHeight  // offset de 70 para el título
            val right = left + cellWidth
            val bottom = top + cellHeight

            // Fondo de color
            fillPaint.color = schedule.color
            canvas.drawRect(left, top, right, bottom, fillPaint)

            // Borde
            canvas.drawRect(left, top, right, bottom, borderPaint)

            // Texto centrado en la celda
            val text = schedule.content
            val maxCharsPerLine = ((cellWidth - 10) / (textPaint.textSize * 0.6f)).toInt()
            val lines = wrapText(text, maxCharsPerLine)

            val totalTextHeight = lines.size * (textPaint.textSize + 2f)
            var textY = top + (cellHeight - totalTextHeight) / 2f + textPaint.textSize

            // Elegir color de texto según luminosidad del fondo
            textPaint.color = if (isColorLight(schedule.color)) Color.BLACK else Color.WHITE

            lines.forEach { line ->
                val textWidth = textPaint.measureText(line)
                val textX = left + (cellWidth - textWidth) / 2f
                canvas.drawText(line, textX, textY, textPaint)
                textY += textPaint.textSize + 2f
            }
        }

        document.finishPage(page)

        // --- Guardar archivo ---
        return try {
            val dir = File(context.cacheDir, "schedules").also { it.mkdirs() }
            val file = File(dir, "horario.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            file
        } catch (e: Exception) {
            document.close()
            e.printStackTrace()
            null
        }
    }

    /** Divide el texto en líneas según el máximo de caracteres por línea. */
    private fun wrapText(text: String, maxChars: Int): List<String> {
        if (maxChars <= 0 || text.length <= maxChars) return listOf(text)
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""
        for (word in words) {
            if ((current + " " + word).trim().length > maxChars) {
                if (current.isNotEmpty()) lines.add(current.trim())
                current = word
            } else {
                current = (current + " " + word).trim()
            }
        }
        if (current.isNotEmpty()) lines.add(current.trim())
        return lines.ifEmpty { listOf(text) }
    }

    /** Devuelve true si el color es claro (para elegir texto negro o blanco). */
    private fun isColorLight(color: Int): Boolean {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b)
        return luminance > 150
    }
}