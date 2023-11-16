package com.example.wordwizard

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                MyDbManager.insertToDb(textView.text.toString(),savedImagePath.toString(),getCreatedTime())
                MyDbManager.closeDb()
                startActivity(Intent(this@RecognizeActivity,
                    MainActivity::class.java).setAction("your.custom.action"))
            }
            imageView.setOnClickListener {
                if (intent.extras != null) {
                    val fragment = ImageFragment()
                    val bundle = Bundle()
                    bundle.putString("uri", cardSaveDataImage)
                    fragment.arguments = bundle
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.recognize_layout, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                else{
                    Toast.makeText(this@RecognizeActivity,"Сохраните и перезайдите",Toast.LENGTH_SHORT).show()
                }

            }
            arrowBack.setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }
            regDown.setOnClickListener{
                val pdfFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/${textView.text.toString().substring(1,11)}.pdf"
                val convert = ConvertTextToPdf(textView.text.toString(),pdfFilePath)
                val outputFilePath = "${this@RecognizeActivity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/textView.pdf"
                convert.convert()
                convert.savePdfToStorage(pdfFilePath,outputFilePath)
                Toast.makeText(this@RecognizeActivity,"Файл находиться в ${Environment.getExternalStorageDirectory()}",Toast.LENGTH_SHORT).show()
            }
            regCopy.setOnClickListener{
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(textView.text.toString(),textView.text.toString())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@RecognizeActivity,"Сopied",Toast.LENGTH_SHORT).show()

            }
            regShare.setOnClickListener{
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT,textView.text.toString())
                startActivity(Intent.createChooser(intent, "Поделиться через:"))
            }
            textView.setOnClickListener {
                textView.requestFocus()
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
                println("Мы тут1")
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
    private fun getCreatedTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return currentDateTime.format(formatter)
    }
}