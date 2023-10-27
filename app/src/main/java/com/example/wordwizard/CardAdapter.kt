package com.example.wordwizard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.wordwizard.databinding.CardItemBinding

class CardAdapter: RecyclerView.Adapter<CardAdapter.CardHolder>() {
    private val cardList = ArrayList<CardData>()

    class CardHolder(item: View):RecyclerView.ViewHolder(item) {
        private val binding = CardItemBinding.bind(item)
        fun bind(cardData: CardData){
            binding.apply {
                    imageCard.setImageURI(cardData.imageId.toUri())
                    textCard.text = cardData.title
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
        holder.bind(cardList[position])
    }
    fun addCard(cardData: ArrayList<CardData>){
        cardList.clear()
        cardList.addAll(cardData)
        notifyDataSetChanged()
    }
}