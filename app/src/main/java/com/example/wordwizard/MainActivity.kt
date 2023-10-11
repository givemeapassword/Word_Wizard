package com.example.wordwizard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.wordwizard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            menu.setOnClickListener {
                drawerLayout.openDrawer(Gravity.LEFT)
                navView.setNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.Licenses -> {}
                        R.id.Privacy -> {}
                        R.id.Share -> {}
                        R.id.Send -> {}
                        R.id.Rate_app -> {}
                        R.id.Terms -> {}
                        else -> {}
                    }
                    true
                }
            }
            imageButton.setOnClickListener {
                BottomSheetDialog().show(supportFragmentManager,"BottomSheetDialog")
            }
        }
    }
}