package com.example.wordwizard


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordwizard.databinding.ActivityMainBinding
import com.example.wordwizard.db.MyDbManager

class MainActivity : AppCompatActivity() {

    private val MyDbManager = MyDbManager(this)
    private lateinit var binding: ActivityMainBinding
    private val adapter = CardAdapter()
    private val taskList = ArrayList<CardData>()
    lateinit var plant: CardData;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("Main","OnCreate Main")

        binding.apply {
            menu.setOnClickListener {
                Log.i("Main","Menu Click")
                drawerLayout.openDrawer(GravityCompat.START)
            }
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
            Log.i("Main","Create RV")
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            taskList.addAll(MyDbManager.getAllCards())
            rcView.adapter = adapter
            adapter.addCard(taskList)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyDbManager.closeDb()
    }
    }



