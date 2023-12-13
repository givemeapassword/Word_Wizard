package com.example.wordwizard

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.wordwizard.common.ConvertTextToPdf
import com.example.wordwizard.common.ConvertTextToPng
import com.example.wordwizard.common.ConvertTextToTxt
import com.example.wordwizard.databinding.ActivityRecognizeBinding
import com.example.wordwizard.db.MyDbManager
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class RecognizeActivity : AppCompatActivity() {
    private val myDbManager = MyDbManager(this)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var binding: ActivityRecognizeBinding
    private lateinit var cardSaveDataImage: String
    private lateinit var uriPhotoPicker: String
    private lateinit var byteArray: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Activity_Recognize)
        binding = ActivityRecognizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** принятие данных карточки и их прорисовка**/
        transferredIntent()
        recognizeButtonBinding()
    }

    private fun transferredIntent() {
        when (intent.action) {
            "Card" -> {
                cardSaveDataImage = intent.getStringExtra("card_image")!!
                val cardSaveData = intent.getStringExtra("card_text")
                binding.apply {
                    imageView.setImageURI(cardSaveDataImage.toUri())
                    textView.text = cardSaveData
                    SaveBtn.visibility = View.GONE
                }
            }
            "Camera" -> {
                launchCamera()
            }
            "Photo" -> {
                uriPhotoPicker = intent.getStringExtra("UriPicker").toString()
                copyToDirectory()
                processImage(InputImage.fromFilePath(this, uriPhotoPicker.toUri()))
                mipmap(uriPhotoPicker.toUri())
                animationDownload()
            }
            //QR
            //INK
        }
    }

    private fun recognizeButtonBinding(){
        binding.apply {
            SaveBtn.setOnClickListener {
                when (intent.action) {
                    "Photo" -> cardSaveDataImage = uriPhotoPicker
                    "Camera" -> cardSaveDataImage = Uri.fromFile(photoFile).toString()
                }
                println(byteArray)
                myDbManager.insertToDb(
                    textView.text.toString(),
                    cardSaveDataImage, getCreatedTime(),byteArray)
                startActivity(
                    Intent(this@RecognizeActivity,
                        MainActivity::class.java
                    ).setAction("SaveCard"))
            }
            imageView.setOnClickListener {
                when (intent.action) {
                    "Photo" -> cardSaveDataImage = uriPhotoPicker
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
            regDown.setOnClickListener {
                val popupMenu = PopupMenu(this@RecognizeActivity, binding.regDown)
                popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.pdf -> {
                            val convert = ConvertTextToPdf(textView.text.toString(),pdfFile.path)
                            val outputFilePath = "${Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOCUMENTS)}/${pdfFile.name}"
                            convert.savePdfToStorage(pdfFile.path,outputFilePath)
                            Toast.makeText(this@RecognizeActivity,"Созданный файл находиться в " +
                                    "${Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOCUMENTS)}",Toast.LENGTH_SHORT).show()
                            true
                        }
                        R.id.png -> {
                            val convert = ConvertTextToPng(textView.text.toString(),pngFile.path)
                            convert.translateTextToPngFile()
                            Toast.makeText(this@RecognizeActivity,"Созданный файл находиться в " +
                                    "${Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOCUMENTS)}",Toast.LENGTH_SHORT).show()
                            true
                        }
                        R.id.txt -> {
                            val convert = ConvertTextToTxt(this@RecognizeActivity,
                                textView.text.toString(),txtFile.path)
                            Toast.makeText(this@RecognizeActivity,"Созданный файл находиться в " +
                                    "${Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOCUMENTS)}",Toast.LENGTH_SHORT).show()
                            convert.saveTextToFile()
                            true
                        }
                        // Добавьте дополнительные пункты меню, если необходимо
                        else -> false
                    }
                }
                popupMenu.show()
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

    private fun copyToDirectory() {
        val inputStream = contentResolver.openInputStream(uriPhotoPicker.toUri())
        val outputStream = FileOutputStream(photoFilePicker)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()
        uriPhotoPicker = Uri.fromFile(photoFilePicker).toString()
    }

    private val photoFile: File by lazy {
        File.createTempFile(
            "image",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        )
    }

    private val photoFilePicker: File by lazy {
        File.createTempFile(
            "image_picker",
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
    private val txtFile: File by lazy {
        File.createTempFile(
            "txt_",
            ".txt",
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        )
    }
    private val pngFile: File by lazy {
        File.createTempFile(
            "png_",
            ".png",
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        )
    }

    private fun launchCamera() {
        try {
            val takeImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoURI = FileProvider.getUriForFile(this,
                "com.example.wordwizard.fileprovider", photoFile)
            takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takeImageLauncher.launch(takeImageIntent)
        } catch (e: Exception) {
            /** Error Camera don't start **/
            println(e)
        }
    }
    private val takeImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if ( result.resultCode == Activity.RESULT_OK ) {

                lifecycleScope.launch {
                    processImage(InputImage.fromFilePath(applicationContext, Uri.fromFile(photoFile)))
                }
                mipmap(photoFile.toUri())
                animationDownload()
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
    private fun animationDownload() {
        lifecycleScope.launch {
            val fragment = DownloadFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.recognize_layout, fragment)
                .addToBackStack(null)
                .commit()
            delay(2000)
            supportFragmentManager.popBackStack()
        }
    }
    private fun getCreatedTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return currentDateTime.format(formatter)
    }

    private fun mipmap(photoUri: Uri){
        val originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        val thumbnailBitmap = ThumbnailUtils.extractThumbnail(originalBitmap,65,65)
        binding.imageView.setImageBitmap(thumbnailBitmap)
        val stream = ByteArrayOutputStream()
        thumbnailBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY,70,stream)
        byteArray = stream.toByteArray()
    }
}