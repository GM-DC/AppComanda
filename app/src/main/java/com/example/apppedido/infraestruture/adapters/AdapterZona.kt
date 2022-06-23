package com.example.apppedido.infraestruture.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.R
import com.example.apppedido.domain.Model.DCZonaItem

class AdapterZona(private val data: List<DCZonaItem>, private val onClickListener: (DCZonaItem) -> Unit): RecyclerView.Adapter<AdapterZona.holderZona>() {

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderZona {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderZona(layoutInflater.inflate(R.layout.item_zona,parent,false))
    }

    override fun onBindViewHolder(holder: holderZona, position: Int) {
        holder.render(data[position], onClickListener, position)

        if (selectedPosition === position) {
            holder.itemView.setBackgroundResource(R.drawable.effect_clic_zona) //banco
        }else {

            holder.itemView.setBackgroundResource(R.drawable.effect_clic_mesa) //negro
        }

        holder.itemView.setOnClickListener {
            onClickListener(data[position])
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderZona(private val view: View):RecyclerView.ViewHolder(view){
        fun render (data: DCZonaItem, onClickListener: (DCZonaItem) -> Unit, position: Int){
            val tx_zona = view.findViewById<TextView>(R.id.tx_zona)
            val iv_zona = view.findViewById<ImageView>(R.id.iv_iconZona)

            tx_zona.text = data.nombreZonas
            tx_zona.setTextColor(Color.parseColor("#0E83C9"))
            iv_zona.setColorFilter(Color.parseColor("#0E83C9"))

        }
    }
}