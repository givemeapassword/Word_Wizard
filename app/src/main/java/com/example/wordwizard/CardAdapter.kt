package com.example.wordwizard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordwizard.databinding.CardItemBinding

class CardAdapter: RecyclerView.Adapter<CardAdapter.CardHolder>() {
    private val cardList = ArrayList<CardData>()

    class CardHolder(item: View):RecyclerView.ViewHolder(item) {
        val binding = CardItemBinding.bind(item)
        fun bind(cardData: CardData){
            binding.apply {
                imageCard.setImageBitmap(cardData.imageId)
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(cardList[position])
    }
    fun addCard(cardData: CardData){
        cardList.clear()
        cardList.addAll(listOf(cardData))
        notifyDataSetChanged()
    }
}