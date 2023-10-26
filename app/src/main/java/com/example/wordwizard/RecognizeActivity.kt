package com.example.wordwizard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.wordwizard.databinding.ActivityRecognizedCardBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class RecognizeActivity : AppCompatActivity() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var binding: ActivityRecognizedCardBinding
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecognizedCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            captureImageBtn.setOnClickListener {
                takeImage()
            }
            SaveBtn.setOnClickListener {
                processImage()
            }
        }
    }

    private fun takeImage() {
        try {
            takeImageLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
    private val takeImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val extras: Bundle? = data?.extras
                imageBitmap = extras?.get("data") as Bitmap
                binding.imageView.setImageBitmap(imageBitmap)
            }
            else{
                //Обработка ошибки
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
                    binding.textView.text = "Something go wrong.Try again."
                }
        }
    }
}