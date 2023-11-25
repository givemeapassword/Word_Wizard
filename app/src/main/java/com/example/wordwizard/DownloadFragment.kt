package com.example.wordwizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wordwizard.databinding.DownloadScreenBinding


class DownloadFragment : Fragment() {
    private lateinit var binding: DownloadScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DownloadScreenBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}