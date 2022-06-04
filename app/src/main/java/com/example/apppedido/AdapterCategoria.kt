package com.example.apppedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterCategoria(private val data: List<DCCategoriaItem>, private val onClickListener: (DCCategoriaItem) -> Unit): RecyclerView.Adapter<AdapterCategoria.holderCategoria>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCategoria.holderCategoria {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AdapterCategoria.holderCategoria(layoutInflater.inflate(R.layout.item_categoria,parent,false))
    }

    override fun onBindViewHolder(holder: holderCategoria, position: Int) {
        holder.render(data[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderCategoria(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DCCategoriaItem, onClickListener: (DCCategoriaItem) -> Unit){
            val tx_categoria = view.findViewById<TextView>(R.id.tx_categoria)
            val iv_categoria = view.findViewById<ImageView>(R.id.iv_iconCategoria)

            tx_categoria.text = data.nameCategoria
            tx_categoria.setTextColor(Color.parseColor("#0E83C9"))
            iv_categoria.setColorFilter(Color.parseColor("#0E83C9"))

            itemView.setOnClickListener { onClickListener(data) }

        }
    }
}