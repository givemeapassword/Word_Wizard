package com.example.wordwizard

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.wordwizard.databinding.ActivityRecognizedCardBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class RecognizedCard : AppCompatActivity() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    lateinit var binding: ActivityRecognizedCardBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecognizedCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            captureImageBtn.setOnClickListener {
                takeImage()
                textView.text = ""
                processImage()
            }
            SaveBtn.setOnClickListener {
            }
        }
    }

    private fun takeImage(){
        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE)
        }
        catch (e:Exception){
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val extras: Bundle? = data?.extras
            imageBitmap= extras?.get("data") as Bitmap
            if (imageBitmap!=null) {
                binding.imageView.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun processImage(){
        if (imageBitmap!=null) {
            val image = imageBitmap?.let {
                InputImage.fromBitmap(it, 0)
            }
            image?.let {
                recognizer.process(it)
                    .addOnSuccessListener { visionText ->
                        binding.textView.text = visionText.text
                    }
                    .addOnFailureListener { e ->
                    }
            }
        }
        else{
            Toast.makeText(this, "Please select photo", Toast.LENGTH_SHORT).show()
        }
    }
}