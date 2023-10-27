package com.example.wordwizard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wordwizard.databinding.ActivityRecognizeBinding
import com.example.wordwizard.db.MyDbManager
import com.example.wordwizard.db.SaveExternalStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayOutputStream

class RecognizeActivity : AppCompatActivity() {
    private val MyDbManager = MyDbManager(this)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var binding: ActivityRecognizeBinding
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("RecognizeActivity","onCreate")

        /** Запуск камеры перед созадние View */
        launchImage()
        binding = ActivityRecognizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            SaveBtn.setOnClickListener {
                MyDbManager.openDb()
                Log.i("RecognizeActivity","SaveButton")
                val intent = Intent(this@RecognizeActivity,
                    MainActivity::class.java).setAction("your.custom.action")
                val savedImagePath = SaveExternalStorage.saveImageToExternalStorage(imageBitmap)
                MyDbManager.insertToDb(textView.text.toString(),savedImagePath.toString())
                startActivity(intent)
                finish()
            }
        }
    }
    private fun launchImage() {
        try {
            takeImageLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } catch (e: Exception) {
            /** Обработка ошибки */
        }
    }
    private val takeImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if ( result.resultCode == Activity.RESULT_OK ) {
                val data: Intent? = result.data
                val extras: Bundle? = data?.extras
                imageBitmap = extras?.get("data") as Bitmap
                binding.imageView.setImageBitmap(imageBitmap)
                processImage()
            }
            else{
                /** Обработка ошибки */
            }
        }
    private fun processImage(){
        val image = imageBitmap.let {
            InputImage.fromBitmap(it, 0)
        }
        image.let {
            recognizer.process(it)
                .addOnSuccessListener { visionText ->
                    binding.textView.text = visionText.text
                }
                .addOnFailureListener {
                    binding.textView.text = R.string.recognize_error.toString()
                }
        }
    }
}