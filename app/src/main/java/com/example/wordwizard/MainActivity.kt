package com.example.wordwizard


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordwizard.card.CardAdapter
import com.example.wordwizard.card.CardData
import com.example.wordwizard.databinding.ActivityMainBinding
import com.example.wordwizard.db.MyDbManager

class MainActivity : AppCompatActivity(),CardAdapter.Listener {

    private val MyDbManager = MyDbManager(this)
    private lateinit var binding: ActivityMainBinding
    private val adapter = CardAdapter(this)
    private val taskList = ArrayList<CardData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            /**кнопка меню**/
            menu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            /**кнопка подписки**/
            RV.setOnClickListener{
                Toast.makeText(this@MainActivity,"В разработке", Toast.LENGTH_SHORT).show()
            }

            /**кнопка навигацинного фрагмента**/
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.Licenses -> { Toast.makeText(this@MainActivity,"В разработке", Toast.LENGTH_SHORT).show() }
                    R.id.Privacy -> {Toast.makeText(this@MainActivity,"В разработке",Toast.LENGTH_SHORT).show()}
                    R.id.Share -> {Toast.makeText(this@MainActivity,"В разработке",Toast.LENGTH_SHORT).show()}
                    R.id.Send -> {Toast.makeText(this@MainActivity,"В разработке",Toast.LENGTH_SHORT).show()}
                    R.id.Rate_app -> {Toast.makeText(this@MainActivity,"В разработке",Toast.LENGTH_SHORT).show()}
                    R.id.Terms -> {Toast.makeText(this@MainActivity,"В разработке",Toast.LENGTH_SHORT).show()}
                    else -> {}
                }
                true
            }

            /**кнопка выбора режима**/
            imageButton.setOnClickListener {
                BottomSheetDialog().show(supportFragmentManager,"BottomSheetDialog")
            }
        }

        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            getSwapMng().attachToRecyclerView(rcView)
            rcView.adapter = adapter
            taskList.addAll(MyDbManager.getAllCards())
            adapter.addCard(taskList)
        }
    }

    override fun onClick(cardData: CardData) {
        startActivity(Intent(this@MainActivity,RecognizeActivity::class.java)
            .setAction("Card")
            .putExtra("card_image",cardData.imageId)
            .putExtra("card_text",cardData.title))
    }

    /** swipe element **/
    private fun getSwapMng(): ItemTouchHelper{
        return ItemTouchHelper(object:ItemTouchHelper
            .SimpleCallback(0,ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeCard(viewHolder.adapterPosition,MyDbManager)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        MyDbManager.closeDb()
    }



}



