package com.example.wordwizard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wordwizard.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Bottom Sheet","OnCreate Bottom Sheet")
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("Bottom Sheet","OnCreateView Bottom Sheet")
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    /*override fun onResume() {
        super.onResume()
        binding.apply {
            Camera.setOnClickListener{}
            Photo.setOnClickListener{}
            DigitalInk.setOnClickListener{}
            QRCode.setOnClickListener{}
        }
    }*/
}