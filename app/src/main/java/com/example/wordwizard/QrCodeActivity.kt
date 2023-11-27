package com.example.wordwizard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.wordwizard.databinding.ActivityQrCodeBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeActivity : AppCompatActivity() {
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()
    private lateinit var binding: ActivityQrCodeBinding
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Activity_QR)
        setContentView(R.layout.activity_qr_code)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_qr_code)
        takeImage.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))

        binding.apply {
            captureImage.setOnClickListener {
                takeImage.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            }
            textView.setOnClickListener{
                textView.requestFocus()
            }
        }
    }

    private val takeImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val extras: Bundle? = data?.extras
            imageBitmap = extras?.get("data") as Bitmap?
            binding.imageView.setImageBitmap(imageBitmap)
            processImage()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun processImage(){
        println(imageBitmap)
        val image = InputImage.fromBitmap(imageBitmap!!, 0)
        val scanner = BarcodeScanning.getClient(options)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.toString()=="[]"){
                    Toast.makeText(this, "Nothing to scan", Toast.LENGTH_SHORT).show()
                }
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_WIFI -> {
                            println("я тут1")
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                            binding.textView.text = "$ssid \n $password \n $type"
                        }
                        Barcode.TYPE_URL -> {
                            println("я тут2")
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
                            println(title)
                            println(url)
                            binding.textView.text = "$url"
                        }
                        Barcode.TYPE_TEXT -> {
                            println("я тут")
                            binding.textView.text = barcode.rawValue.toString()
                        }
                    }
                }
                shareText()
            }
            .addOnFailureListener {
                Toast.makeText(this@QrCodeActivity,"Something go wrong",Toast.LENGTH_SHORT).show()
            }
    }
    private fun shareText(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,binding.textView.text.toString())
        startActivity(Intent.createChooser(intent, "Поделиться текстом:"))
    }
}