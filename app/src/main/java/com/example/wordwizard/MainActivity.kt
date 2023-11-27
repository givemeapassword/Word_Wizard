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

    private val myDbManager = MyDbManager(this)
    private lateinit var binding: ActivityMainBinding
    private val adapter = CardAdapter(this)
    private val cardList = ArrayList<CardData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WordWizard)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainButtonsBinding()
        recyclerViewBinding()
    }

    private fun mainButtonsBinding(){
        binding.apply {

            /**кнопка меню**/
            menu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            /**кнопка подписки**/
            subscription.setOnClickListener{
                toastInDeveloping()
            }
            /**кнопка навигацинного фрагмента**/
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.Privacy -> {toastInDeveloping()}
                    R.id.Licenses -> {toastInDeveloping()}
                    R.id.Share -> {toastInDeveloping()}
                    R.id.Send -> {toastInDeveloping()}
                    R.id.Rate_app -> {toastInDeveloping()}
                    R.id.Terms -> {toastInDeveloping()}
                }
                true
            }
            /**кнопка выбора режима**/
            imageButton.setOnClickListener {
                BottomSheetDialog().show(supportFragmentManager,"BottomSheetDialog")
            }
        }
    }

    private fun recyclerViewBinding(){
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            rcView.setHasFixedSize(true)
            rcView.adapter = adapter
            getSwapMng().attachToRecyclerView(rcView)
            cardList.addAll(myDbManager.getAllCards())
            adapter.addCard(cardList)
        }
    }

    override fun onClick(cardData: CardData) {
        startActivity(Intent(this@MainActivity,RecognizeActivity::class.java)
            .setAction("Card")
            .putExtra("card_image",cardData.imageId)
            .putExtra("card_text",cardData.title)
        )
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
                adapter.removeCard(viewHolder.adapterPosition,myDbManager)
            }
        })
    }

    private fun toastInDeveloping() = Toast
        .makeText(this@MainActivity,"In developing", Toast.LENGTH_SHORT).show()
}



