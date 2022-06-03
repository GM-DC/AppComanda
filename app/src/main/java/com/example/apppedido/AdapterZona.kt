package com.example.apppedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class AdapterZona(private val data: List<DCZonaItem>, private val onClickListener: (DCZonaItem) -> Unit): RecyclerView.Adapter<AdapterZona.holderZona>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderZona {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderZona(layoutInflater.inflate(R.layout.item_zona,parent,false))
    }

    override fun onBindViewHolder(holder: holderZona, position: Int) {
        holder.render(data[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderZona(private val view: View):RecyclerView.ViewHolder(view){
        fun render (data: DCZonaItem, onClickListener: (DCZonaItem) -> Unit){
            val tx_zona = view.findViewById<TextView>(R.id.tx_zona)
            val iv_zona = view.findViewById<ImageView>(R.id.iv_iconZona)

            tx_zona.text = data.name
            tx_zona.setTextColor(Color.parseColor("#0E83C9"))
            iv_zona.setColorFilter(Color.parseColor("#0E83C9"))

            itemView.setOnClickListener { onClickListener(data) }
        }
    }
}