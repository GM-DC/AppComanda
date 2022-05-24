package com.example.apppedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterPedido(private val data: List<DataClassPedido>): RecyclerView.Adapter<AdapterPedido.holderPedido>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPedido.holderPedido {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AdapterPedido.holderPedido(layoutInflater.inflate(R.layout.item_pedido,parent,false))
    }

    override fun onBindViewHolder(holder: holderPedido, position: Int) {
        holder.render(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderPedido(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DataClassPedido){
            val tv_nombrePlato = view.findViewById<TextView>(R.id.tv_nombrePlato)
            val tv_precio = view.findViewById<TextView>(R.id.tv_precio)


            tv_nombrePlato.text = data.namePlato
            tv_precio.text = data.precio.toString()
        }
    }
}