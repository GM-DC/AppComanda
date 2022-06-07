package com.example.apppedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class AdapterMesa(private val data: ArrayList<DCMesaItem>,private val onClickListener: (DCMesaItem) -> Unit): RecyclerView.Adapter<AdapterMesa.holderMesa>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderMesa {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderMesa(layoutInflater.inflate(R.layout.item_mesa,parent,false))
    }

    override fun onBindViewHolder(holder: holderMesa, position: Int) {

        holder.render(data[position],onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size

    }

    class holderMesa(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DCMesaItem,onClickListener: (DCMesaItem) -> Unit){
            val tx_mesa = view.findViewById<TextView>(R.id.tx_mesa)
            val iv_mesa = view.findViewById<ImageView>(R.id.iv_iconMesa)

                tx_mesa.text = data.idMesa.toString()
                tx_mesa.setTextColor(Color.parseColor("#0E83C9"))
                iv_mesa.setColorFilter(Color.parseColor("#0E83C9"))

            itemView.setOnClickListener { onClickListener(data) }
        }

    }
}