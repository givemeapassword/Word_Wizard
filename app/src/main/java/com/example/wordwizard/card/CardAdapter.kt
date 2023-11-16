package com.example.wordwizard.card


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
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
                    imageCard.setImageURI(cardData.imageId.toUri())
                    textCard.text = cardData.title
                    cardId.setOnClickListener{
                        listener.onClick(cardData)
                    }
                    cardShare.setOnClickListener{
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT,textCard.text.toString())
                        cardShare.context.startActivity(Intent.createChooser(intent, "Поделиться через:"))
                    }
                    cardCopy.setOnClickListener{
                        val clipboard = cardId.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText(textCard.text.toString(),textCard.text.toString())
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(cardId.context,"Сopied", Toast.LENGTH_SHORT).show()
                    }
            }
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
        notifyDataSetChanged()
    }

    fun removeCard(position: Int,dbManager: MyDbManager){
        dbManager.deleteFromDb(cardList[position].id.toString())
        cardList.removeAt(position)
        notifyItemRangeChanged(0,cardList.size)
        notifyItemRemoved(position)
    }

    interface Listener{
        fun onClick(cardData: CardData)
    }

}