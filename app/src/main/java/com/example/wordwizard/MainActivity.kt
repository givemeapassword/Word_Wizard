package com.example.wordwizard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.wordwizard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Main","OnCreate Main")

        //надувание вьюшки
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //запрос разрешения у пользователя


        //слушатели драйверлейлаута и боттомшитдиалога
        binding.apply {
            menu.setOnClickListener {
                Log.i("Main","Menu Click")
                drawerLayout.openDrawer(GravityCompat.START)
            }
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.Licenses -> {Toast.makeText(this@MainActivity,"Licenses", Toast.LENGTH_SHORT).show()}
                    R.id.Privacy -> {Toast.makeText(this@MainActivity,"Privacy", Toast.LENGTH_SHORT).show()}
                    R.id.Share -> {Toast.makeText(this@MainActivity,"Share", Toast.LENGTH_SHORT).show()}
                    R.id.Send -> {Toast.makeText(this@MainActivity,"Send", Toast.LENGTH_SHORT).show()}
                    R.id.Rate_app -> {Toast.makeText(this@MainActivity,"Rate_app", Toast.LENGTH_SHORT).show()}
                    R.id.Terms -> {Toast.makeText(this@MainActivity,"Terms", Toast.LENGTH_SHORT).show()}
                    else -> {}
                }
                true
            }
            imageButton.setOnClickListener {
                Log.i("Main","ImageButton Click")
                BottomSheetDialog().show(supportFragmentManager,"BottomSheetDialog")
            }
        }
    }



}