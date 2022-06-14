package com.example.apppedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class AdapterPlatoFiltrado(private val data: ArrayList<DCPlatoItem>, private val onClickListener: (DCPlatoItem) -> Unit): RecyclerView.Adapter<AdapterPlatoFiltrado.holderPlatoFiltrado>() {

    private var listaPlatoFiltados = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPlatoFiltrado.holderPlatoFiltrado {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderPlatoFiltrado(layoutInflater.inflate(R.layout.item_plato,parent,false))
    }

    override fun onBindViewHolder(holder: holderPlatoFiltrado, position: Int) {
        holder.render(data[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderPlatoFiltrado(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DCPlatoItem, onClickListener: (DCPlatoItem) -> Unit){
            val tx_plato = view.findViewById<TextView>(R.id.tx_plato)
            val tx_platoPrecio = view.findViewById<TextView>(R.id.tx_platoPrecio)

            val iv_iconPlato = view.findViewById<ImageView>(R.id.iv_iconPlato)

            tx_platoPrecio.text = "S/. ${data.PrecioVenta}"
            tx_plato.text = data.namePlato
            tx_platoPrecio.setTextColor(Color.parseColor("#FF041C9E"))
            tx_plato.setTextColor(Color.parseColor("#FF041C9E"))
            iv_iconPlato.setColorFilter(Color.parseColor("#FF041C9E"))

            itemView.setOnClickListener{onClickListener(data)}
        }
    }
}