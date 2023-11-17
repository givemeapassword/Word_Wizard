package com.example.wordwizard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.wordwizard.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentBottomSheetDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPermsissionAndCheck()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Bottom Sheet","OnCreate Bottom Sheet")

        /** установка выдвижной темы */
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

        /** биндинг кнопок слушателей */
        binding.apply {
            Camera.setOnClickListener {
                startActivity(Intent(context,RecognizeActivity::class.java).setAction("Camera"))
            }
            Photo.setOnClickListener {
                startActivity(Intent(context,RecognizeActivity::class.java).setAction("Photo"))
            }
            //QR
            //INK
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
                Toast.makeText(context,"Camera can run", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.i("BottomSheetDialog","Denied permission")
                Toast.makeText(context,"permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        checkCameraPermission()
    }

    private fun checkCameraPermission(){
        when{
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
}