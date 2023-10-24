package com.example.wordwizard

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.wordwizard.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentBottomSheetDialogBinding
    private val PHOTO_REQUEST_CODE = 100;
    private val PHOTO_PERMISSIONS_REQUEST_CODE = 200;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPermsissionListener()
        checkCameraPermission()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Bottom Sheet","OnCreate Bottom Sheet")
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
        binding.apply {
            Camera.setOnClickListener {
                val intent = Intent(context,RecognizedCard::class.java).setAction("your.custom.action")
                startActivity(intent)
            }
            Photo.setOnClickListener{
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("Bottom Sheet","OnCreateView Bottom Sheet")
        binding = FragmentBottomSheetDialogBinding.inflate(inflater,container,false)
        return binding.root
    }



    private fun checkCameraPermission(){
        when{
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            -> {
                /*startActivityForResult(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                    PHOTO_REQUEST_CODE
                )*/
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),PHOTO_PERMISSIONS_REQUEST_CODE
                )
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun registerPermsissionListener(){
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            if(it){
                Toast.makeText(context,"Camera can run", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}