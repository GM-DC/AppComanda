package com.example.apppedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
            val btn_plato = view.findViewById<Button>(R.id.tx_plato)

            btn_plato.text = data.name
            println("hola")

            btn_plato.setOnClickListener {
                println("hola 2")
                onClickListener(data)
            }
        }


    }
}