package com.example.apppedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterPlato(private val data: List<DataClassPlato>, private val onClickListener: (DataClassPlato) -> Unit): RecyclerView.Adapter<AdapterPlato.holderPlato>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPlato.holderPlato {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AdapterPlato.holderPlato(layoutInflater.inflate(R.layout.item_plato,parent,false))
    }

    override fun onBindViewHolder(holder: holderPlato, position: Int) {
        holder.render(data[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderPlato(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DataClassPlato, onClickListener: (DataClassPlato) -> Unit){
            val tx_plato = view.findViewById<TextView>(R.id.tx_plato)
            val iv_iconPlato = view.findViewById<ImageView>(R.id.iv_iconPlato)

            tx_plato.text = data.name
            tx_plato.setTextColor(Color.parseColor("#0E83C9"))
            iv_iconPlato.setColorFilter(Color.parseColor("#0E83C9"))


            itemView.setOnClickListener {onClickListener(data)}
        }
    }
}