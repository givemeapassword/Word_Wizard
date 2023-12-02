package com.example.wordwizard.card

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wordwizard.R
import com.example.wordwizard.databinding.CardItemBinding
import com.example.wordwizard.db.MyDbManager

class CardAdapter(private val listener: Listener): RecyclerView.Adapter<CardAdapter.CardHolder>() {
    private val cardList = ArrayList<CardData>()

    class CardHolder(item: View):RecyclerView.ViewHolder(item) {
        private val binding = CardItemBinding.bind(item)
        fun bind(cardData: CardData, listener: Listener){
            binding.apply {

                /**инициализация всех данных карточки**/
                decodeByteArrayToBitmap(cardData.imageMipmapId)
                imageCard.setImageBitmap(decodeByteArrayToBitmap(cardData.imageMipmapId))
                textCard.text = cardData.title
                timeCard.text = cardData.date

                /**инициализация нажатия на саму картчку**/
                cardId.setOnClickListener{
                    listener.onClick(cardData)
                }

                /**инициализация нажатия на "поделиться"**/
                cardShare.setOnClickListener{
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT,textCard.text.toString())
                    }
                    cardShare.context.startActivity(Intent.createChooser(intent, "Поделиться через:"))
                }
                /**инициализация нажатия на "копировать"**/
                cardCopy.setOnClickListener{
                    val clipboard = cardId.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText(textCard.text.toString(),textCard.text.toString())
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(cardId.context,"Скопировано", Toast.LENGTH_SHORT).show()
                }
            }
        }
        private fun decodeByteArrayToBitmap(byteArray: ByteArray): Bitmap {
            return  BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item,parent,false)
        return CardHolder(view)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(cardList[position],listener)
    }

    fun addCard(cardData: ArrayList<CardData>){
        cardList.clear()
        cardList.addAll(cardData)
    }

    fun removeCard(position: Int,dbManager: MyDbManager){
        dbManager.deleteFromDb(cardList[position].id, cardList[position].imageId)
        cardList.removeAt(position)
        notifyItemRangeChanged(0,cardList.size)
        notifyItemRemoved(position)
    }


    interface Listener{
        fun onClick(cardData: CardData)
    }

}