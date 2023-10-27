package com.example.wordwizard


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordwizard.databinding.ActivityMainBinding
import com.example.wordwizard.db.MyDbManager
import com.example.wordwizard.db.SaveExternalStorage

class MainActivity : AppCompatActivity() {

    val MyDbManager = MyDbManager(this)
    private lateinit var binding: ActivityMainBinding
    private lateinit var text: String
    private val adapter = CardAdapter()
    private val taskList: ArrayList<CardData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Main","OnCreate Main")

        /** надувание вьюшки */
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)

        /** запрос разрешения у пользователя */


        /**слушатели драйверлейлаута и боттомшитдиалога */
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

    override fun onResume() {
        super.onResume()
        Log.i("Main","onResume")
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            rcView.adapter = adapter
            Log.i("Main","Create RV")
        }
        taskList.addAll(MyDbManager.getAllCards())
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        MyDbManager.closeDb()
    }
    }



