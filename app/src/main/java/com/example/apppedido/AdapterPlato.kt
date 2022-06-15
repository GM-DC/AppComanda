package com.example.apppedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class AdapterPlato(val data: ArrayList<DCPlatoItem>, private val onClickListener: (DCPlatoItem) -> Unit): RecyclerView.Adapter<AdapterPlato.holderPlato>() {

    var countryFilterList = ArrayList<DCPlatoItem>()

    init {
        countryFilterList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPlato.holderPlato {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderPlato(layoutInflater.inflate(R.layout.item_plato,parent,false))
    }

    override fun onBindViewHolder(holder: holderPlato, position: Int) {
        holder.render(data[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderPlato(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DCPlatoItem, onClickListener: (DCPlatoItem) -> Unit){
            val tx_plato = view.findViewById<TextView>(R.id.tx_plato)
            val tx_platoPrecio = view.findViewById<TextView>(R.id.tx_platoPrecio)

            val iv_iconPlato = view.findViewById<ImageView>(R.id.iv_iconPlato)

            tx_platoPrecio.text = "S/. ${data.PrecioVenta}"
            tx_plato.text = data.namePlato
            tx_platoPrecio.setTextColor(Color.parseColor("#0E83C9"))
            tx_plato.setTextColor(Color.parseColor("#0E83C9"))
            iv_iconPlato.setColorFilter(Color.parseColor("#0E83C9"))

            itemView.setOnClickListener{onClickListener(data)}
        }
    }

    fun filtrado(tx_buscar:String){
        var longitud = tx_buscar.length
        if (longitud == 0){
            data.clear()
            data.addAll(data)
        }else{
            for(i in data.indices){
                if(data[i].namePlato.lowercase().contains(tx_buscar.lowercase())){
                    data.add(data[i])
                }
            }
        }
        notifyDataSetChanged()
    }


}