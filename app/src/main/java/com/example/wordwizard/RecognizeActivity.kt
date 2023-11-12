package com.example.wordwizard

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
    private lateinit var cardSaveDataImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("RecognizeActivity","onCreate")
        binding = ActivityRecognizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** принятие данных карточки и их прорисовка**/
        if ( intent.extras != null) {
            cardSaveDataImage = intent.getStringExtra("card_image")!!
            val cardSaveData = intent.getStringExtra("card_text")
            binding.apply {
                imageView.setImageURI(cardSaveDataImage.toUri())
                textView.text = cardSaveData
                SaveBtn.visibility = View.GONE
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
                val savedImagePath = SaveExternalStorage.saveImageToExternalStorage(imageBitmap)
                MyDbManager.insertToDb(textView.text.toString(),savedImagePath.toString())
                MyDbManager.closeDb()
                startActivity(Intent(this@RecognizeActivity,
                    MainActivity::class.java).setAction("your.custom.action"))
            }
            imageView.setOnClickListener {
                val fragment = ImageFragment()
                val bundle = Bundle()
                bundle.putString("uri",cardSaveDataImage)
                fragment.arguments = bundle
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.recognize_image_layout,fragment)
                    .commit()

            }
            arrowBack.setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }
            regDown.setOnClickListener{

            }
            regCopy.setOnClickListener{
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(textView.text.toString(),textView.text.toString())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@RecognizeActivity,"Сopied",Toast.LENGTH_SHORT).show()

            }
            regShare.setOnClickListener{

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
                if (visionText.text.trim().isEmpty()){
                    binding.textView.text = "*Empty Text*"
                }
                else{
                    binding.textView.text = visionText.text
                }
            }
            .addOnFailureListener {
                binding.textView.text = R.string.recognize_error.toString()
            }
    }
}