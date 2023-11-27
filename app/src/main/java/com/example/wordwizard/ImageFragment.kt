package com.example.wordwizard

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wordwizard.databinding.RecognizeImageBinding


class ImageFragment : Fragment() {

    private lateinit var binding: RecognizeImageBinding
    private lateinit var image: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        image = arguments?.getString("uri")!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecognizeImageBinding.inflate(inflater,container,false)
        binding.imageRegLarge.setImageURI(Uri.parse(image))
        return binding.root
    }

}