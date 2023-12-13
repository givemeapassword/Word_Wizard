package com.example.wordwizard.common

import android.content.Context
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

class ConvertTextToTxt(val context: Context, val textView: String, val fileName: String) {
    fun saveTextToFile() {
        val file = File(fileName)

        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(textView.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}