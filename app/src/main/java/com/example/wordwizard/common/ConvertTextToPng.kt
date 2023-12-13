package com.example.wordwizard.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.File
import java.io.FileOutputStream

class ConvertTextToPng(val text: String, private val fileName: String) {
    fun translateTextToPngFile() {
        val paint = TextPaint()
        paint.color = Color.WHITE
        paint.textSize = 40f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val bitmap = Bitmap.createBitmap(textBounds.width(), textBounds.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val textLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, textBounds.width())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()

        textLayout.draw(canvas)

        val file = File(fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }
}