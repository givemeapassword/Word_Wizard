package com.example.wordwizard

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wordwizard.databinding.DowloadScreenBinding


class DownloadActivity : Fragment() {
    private lateinit var binding: DowloadScreenBinding
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DowloadScreenBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}