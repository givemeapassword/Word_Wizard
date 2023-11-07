package com.example.wordwizard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.wordwizard.databinding.ActivityRecognizeBinding
import com.example.wordwizard.db.MyDbManager
import com.example.wordwizard.db.SaveExternalStorage
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class RecognizeActivity : AppCompatActivity() {
    private val MyDbManager = MyDbManager(this)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var binding: ActivityRecognizeBinding
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("RecognizeActivity","onCreate")
        binding = ActivityRecognizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** принятие данных карточки и их прорисовка**/
        if ( intent.extras != null) {
            val cardSaveDataImage = intent.getStringExtra("card_image")
            val cardSaveData = intent.getStringExtra("card_text")
            binding.apply {
                imageView.setImageURI(cardSaveDataImage?.toUri())
                textView.text = cardSaveData
            }
        }
        else {
            /** Запуск камеры перед созадние View */
            launchImage()
        }
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
            /** Error Camera don't start **/
        }
    }
    private val takeImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if ( result.resultCode == Activity.RESULT_OK ) {
                val data: Intent? = result.data
                val extras: Bundle? = data?.extras
                imageBitmap = extras?.get("data") as Bitmap
                processImage( InputImage.fromBitmap(imageBitmap, 0))
                binding.imageView.setImageBitmap(imageBitmap)
            }
            else{
                onBackPressedDispatcher.onBackPressed()
            }
        }
    private fun processImage(image: InputImage): Task<Text>{
        return recognizer.process(image)
            .addOnSuccessListener { visionText ->
                binding.textView.text = visionText.text
            }
            .addOnFailureListener {
                binding.textView.text = R.string.recognize_error.toString()
            }
    }
}