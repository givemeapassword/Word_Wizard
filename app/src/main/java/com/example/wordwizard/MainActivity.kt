package com.example.wordwizard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordwizard.databinding.ActivityMainBinding
import com.example.wordwizard.db.MyDbHelper
import com.example.wordwizard.db.MyDbManager

class MainActivity : AppCompatActivity() {

    val MyDbManager = MyDbManager(this)
    private lateinit var binding: ActivityMainBinding
    private val adapter = CardAdapter()
    private lateinit var imageCard: Bitmap
    private lateinit var text: String
    private lateinit var byteArray: ByteArray

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

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("image")) {
            getDataRecognizeActivity()
            initRecyclerCard()
            MyDbManager.openDb()
            MyDbManager.insertToDb(text,byteArray)
        }
    }
    private fun initRecyclerCard(){
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            rcView.adapter = adapter
        }
        val cardData = CardData(imageCard,text)
        adapter.addCard(cardData)
        adapter.notifyDataSetChanged()

    }

    private fun getDataRecognizeActivity() {
            byteArray = intent.getByteArrayExtra("image")!!
            text = intent.getStringExtra("text").toString()
            imageCard = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size ?: 0)
        }


    override fun onDestroy() {
        super.onDestroy()
        MyDbManager.closeDb()
    }
    }



