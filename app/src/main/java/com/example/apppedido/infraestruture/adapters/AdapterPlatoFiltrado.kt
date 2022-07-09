package com.example.apppedido.infraestruture.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.R
import com.example.apppedido.domain.Model.DCPlatoItem
import java.util.*

class AdapterPlatoFiltrado(var data: ArrayList<DCPlatoItem>, private val onClickListener: (DCPlatoItem) -> Unit): RecyclerView.Adapter<AdapterPlatoFiltrado.holderPlatoFiltrado>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderPlatoFiltrado {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderPlatoFiltrado(layoutInflater.inflate(R.layout.item_platobuscado,parent,false))
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

            tx_platoPrecio.text = "S/. ${data.preciO_VENTA}"
            tx_plato.text = data.nombre

            itemView.setOnClickListener{onClickListener(data)}
        }
    }

    //to filter the list
    fun filterList(namePlatos: ArrayList<DCPlatoItem>) {
        data = namePlatos
        notifyDataSetChanged()
    }

}