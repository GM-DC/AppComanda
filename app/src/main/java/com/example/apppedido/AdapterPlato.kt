package com.example.apppedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class AdapterPlato(private val data: ArrayList<DCPlatoItem>, private val onClickListener: (DCPlatoItem) -> Unit): RecyclerView.Adapter<AdapterPlato.holderPlato>() {



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

    //-------  PRUEBA ------------
    open fun filter(text: String) {
        println("hola 22")
        var text = text
        if (text.isEmpty()) {
            println("hola 333")
            data.clear()
            data.addAll(data)
        } else {
            val result: java.util.ArrayList<DCPlatoItem> = java.util.ArrayList()
            text = text.lowercase(Locale.getDefault())
            for (i in data.indices) {
                //match by name or phone
                if (data[i].namePlato.lowercase().contains(text) || data[i].namePlato.lowercase().contains(text)) {
                    result.add(data[i])
                }
            }
            data.clear()
            data.addAll(result)
        }
        notifyDataSetChanged()
    }



    class holderPlato(private val view: View): RecyclerView.ViewHolder(view){
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