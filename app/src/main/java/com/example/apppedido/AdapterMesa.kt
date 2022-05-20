package com.example.apppedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class AdapterMesa(private val data: List<DataClassMesa>): RecyclerView.Adapter<AdapterMesa.holderMesa>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMesa.holderMesa {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AdapterMesa.holderMesa(layoutInflater.inflate(R.layout.item_mesa,parent,false))
    }

    override fun onBindViewHolder(holder: holderMesa, position: Int) {
        holder.render(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderMesa(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DataClassMesa){
            val btn_mesa = view.findViewById<Button>(R.id.tx_mesa)
            btn_mesa.text = data.name
        }
    }
}