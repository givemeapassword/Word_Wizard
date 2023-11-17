package com.example.wordwizard

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.wordwizard.common.ConvertTextToPdf
import com.example.wordwizard.databinding.ActivityRecognizeBinding
import com.example.wordwizard.db.MyDbManager
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class RecognizeActivity : AppCompatActivity() {
    private val MyDbManager = MyDbManager(this)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var binding: ActivityRecognizeBinding
    private lateinit var cardSaveDataImage: String
    private var uriPhotoPicker: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("RecognizeActivity","onCreate")
        binding = ActivityRecognizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** принятие данных карточки и их прорисовка**/
        when (intent.action) {
            "Card" -> {
                Log.i("RecognizeActivity","Просмотр сохраненной карточки")
                cardSaveDataImage = intent.getStringExtra("card_image")!!
                val cardSaveData = intent.getStringExtra("card_text")
                binding.apply {
                    imageView.setImageURI(cardSaveDataImage.toUri())
                    textView.text = cardSaveData
                    SaveBtn.visibility = View.GONE
                }
            }
            "Camera" -> {
                Log.i("RecognizeActivity","Режим камеры")
                launchCamera()
            }
            "Photo" -> {
                Log.i("RecognizeActivity","Режим фото")
                uriPhotoPicker = intent.getStringExtra("UriPicker")
                binding.imageView.setImageURI(uriPhotoPicker?.toUri())
                processImage(InputImage.fromFilePath(this,uriPhotoPicker!!.toUri()))

            }
            //QR
            //INK
        }

        binding.apply {

            SaveBtn.setOnClickListener {
                MyDbManager.openDb()
                Log.i("RecognizeActivity", "SaveButton")
                MyDbManager.insertToDb(
                    textView.text.toString(),
                    Uri.fromFile(photoFile).toString(), getCreatedTime()
                )
                MyDbManager.closeDb()
                startActivity(
                    Intent(
                        this@RecognizeActivity,
                        MainActivity::class.java
                    ).setAction("your.custom.action")
                )
            }

            imageView.setOnClickListener {
                when (intent.action) {
                    "Photo" -> cardSaveDataImage = uriPhotoPicker.toString()
                    "Camera" -> cardSaveDataImage = Uri.fromFile(photoFile).toString()
                }
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

            arrowBack.setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }

            regDown.setOnClickListener{
                val convert = ConvertTextToPdf(textView.text.toString(),pdfFile.path)
                val outputFilePath = "${Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS)}/${pdfFile.name}"
                convert.savePdfToStorage(pdfFile.path,outputFilePath)
                Toast.makeText(this@RecognizeActivity,"Созданный файл находиться в ${Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS)}",Toast.LENGTH_SHORT).show()
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
    private val photoFile: File by lazy {
        File.createTempFile(
            "image",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        )
    }
    private val pdfFile: File by lazy {
        File.createTempFile(
            "pdf_",
            ".pdf",
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        )
    }

    private fun launchCamera() {
        try {
            val takeImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoURI = FileProvider.getUriForFile(this, "com.example.wordwizard.fileprovider", photoFile)
            takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takeImageLauncher.launch(takeImageIntent)
        } catch (e: Exception) {
            println(e)
            /** Error Camera don't start **/
        }
    }


    private val takeImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if ( result.resultCode == Activity.RESULT_OK ) {
                processImage(InputImage.fromFilePath(this, Uri.fromFile(photoFile)))
                binding.imageView.setImageURI(Uri.fromFile(photoFile))
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
    private fun getCreatedTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return currentDateTime.format(formatter)
    }
}