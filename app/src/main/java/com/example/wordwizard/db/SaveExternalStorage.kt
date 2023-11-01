package com.example.wordwizard.db
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object SaveExternalStorage {
    private var imageFile:File? = null
    private var successCheck = true
    fun saveImageToExternalStorage(bitmap: Bitmap): Uri {

        /** Особенности защищенности данных в Android 11+ */
        val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),"Word_Wizard");
        } else {
            /**Запись в Android -11 */
            File(Environment.getExternalStorageDirectory(),"Word_Wizard");
        }

        /** Если директории не существует */
        if (!storageDir.exists()) {
            Log.i("SaveInStorage","This directory don't exist...Creating.")
            successCheck = storageDir.mkdirs()
        }

        /** Нейминг файлов */
        if (successCheck) {
             naming(storageDir = storageDir)
            }

        /** Создание файла в хранилище */
        try {
            Log.i("SaveInStorage","Creating File in JPEG")
            val fOut = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.close()
            } catch (e: IOException) {
                Log.d("SaveInStorage","SOMETHING WENT WRONG IN CREATING THE FILE")
                e.printStackTrace()
            }

        Log.i("SaveInStorage","uri transfer: ${Uri.fromFile(imageFile)}")
        return Uri.fromFile(imageFile)
    }

    private fun naming(index:Int = 1, storageDir:File ):File{
        return if (File(storageDir, "image${index}.jpg").exists()) {
            var index2 = index
            naming(++index2,storageDir)
        } else {
            imageFile = File(storageDir, "image${index}.jpg")
            imageFile as File
        }
    }
}