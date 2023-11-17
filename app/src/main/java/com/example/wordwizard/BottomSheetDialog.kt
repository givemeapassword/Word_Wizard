package com.example.wordwizard

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.security.Permission
import java.security.Permissions


class BottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentBottomSheetDialogBinding
    private lateinit var pickImage:ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPermsissionAndCheck()
        registerImagePicker()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Bottom Sheet","OnCreate Bottom Sheet")

        /** установка выдвижной темы */
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

        /** биндинг кнопок слушателей режимов */
        binding.apply {
            Camera.setOnClickListener {
                startActivity(Intent(context,RecognizeActivity::class.java).setAction("Camera"))
            }
            Photo.setOnClickListener {
                val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            }
            QRCode.setOnClickListener{
                Toast.makeText(context,"В разработке", Toast.LENGTH_SHORT).show()
            }
            DigitalInk.setOnClickListener{
                Toast.makeText(context,"В разработке", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("Bottom Sheet","OnCreateView Bottom Sheet")
        binding = FragmentBottomSheetDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun registerPermsissionAndCheck(){
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            if(it){
                Log.i("BottomSheetDialog","Given permission")
                Toast.makeText(context,"Камера может быть запущена", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.i("BottomSheetDialog","Denied permission")
                Toast.makeText(context,"В разрешении отказано", Toast.LENGTH_SHORT).show()
            }
        }
        checkCameraPermission()
    }

    private fun checkCameraPermission(){

            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
               Log.i("BottomSheetDialog","Разрешение имеется")
            }
            else{
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
    }
    private fun registerImagePicker(){
        pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
            if (uri != null){
                Log.d("PhotoPicker", "Selected URI: $uri")
                selectedImageUri = uri
                startActivity(Intent(context,RecognizeActivity::class.java)
                    .setAction("Photo")
                    .putExtra("UriPicker",selectedImageUri.toString()))
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

}