package com.example.wordwizard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import com.example.wordwizard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("Main","OnCreate Main")
        binding.apply {
            menu.setOnClickListener {
                Log.i("Main","Menu Click")
                drawerLayout.openDrawer(Gravity.LEFT)
                navView.setNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.Licenses -> {TODO()}
                        R.id.Privacy -> {TODO()}
                        R.id.Share -> {TODO()}
                        R.id.Send -> {TODO()}
                        R.id.Rate_app -> {TODO()}
                        R.id.Terms -> {TODO()}
                        else -> {}
                    }
                    true
                }
            }
            imageButton.setOnClickListener {
                Log.i("Main","ImageButton Click")
                BottomSheetDialog().show(supportFragmentManager,"BottomSheetDialog")
            }
        }
    }
}