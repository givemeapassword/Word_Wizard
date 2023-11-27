package com.example.wordwizard

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.wordwizard.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentBottomSheetDialogBinding
    private lateinit var pickImage:ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPermsissionAndCheck()
        registerImagePicker()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme) //установка выдвижной темы
        regimeBinding()
    }

    private fun regimeBinding(){
        binding.apply {
            Camera.setOnClickListener {
                startActivity(Intent(context,
                    RecognizeActivity::class.java).setAction("Camera"))
            }
            Photo.setOnClickListener {
                pickImage.launch(PickVisualMediaRequest
                    (ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            QRCode.setOnClickListener{
                startActivity(Intent(context,
                    QrCodeActivity::class.java).setAction("Camera"))
            }
            DigitalInk.setOnClickListener{
                toastInDeveloping()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDialogBinding
            .inflate(inflater,container,false)
        return binding.root
    }

    private fun registerPermsissionAndCheck(){
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){}
        checkCameraPermission()
    }

    private fun checkCameraPermission(){
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA)
            -> {
                Log.i("BottomSheetDialog","Given permission")
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun registerImagePicker(){
        pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
            when(uri){
                null -> {
                    Log.d("PhotoPicker", "No media selected")
                }
                else -> {
                    val selectedImageUri =  uri.toString()
                    startActivity(Intent(context,RecognizeActivity::class.java)
                        .setAction("Photo")
                        .putExtra("UriPicker",selectedImageUri))
                }
            }
        }
    }

    private fun toastInDeveloping() = Toast.makeText(activity,
        "In developing", Toast.LENGTH_SHORT).show()
}