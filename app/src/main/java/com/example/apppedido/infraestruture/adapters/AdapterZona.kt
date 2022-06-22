package com.example.apppedido.infraestruture.adapters

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.R
import com.example.apppedido.domain.Model.DCZonaItem

class AdapterZona(private val data: List<DCZonaItem>, private val onClickListener: (DCZonaItem) -> Unit): RecyclerView.Adapter<AdapterZona.holderZona>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderZona {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderZona(layoutInflater.inflate(R.layout.item_zona,parent,false))
    }

    override fun onBindViewHolder(holder: holderZona, position: Int) {
        holder.render(data[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderZona(private val view: View):RecyclerView.ViewHolder(view){
        fun render (data: DCZonaItem, onClickListener: (DCZonaItem) -> Unit){
            val tx_zona = view.findViewById<TextView>(R.id.tx_zona)
            val iv_zona = view.findViewById<ImageView>(R.id.iv_iconZona)
            val cv_zona = view.findViewById<CardView>(R.id.cv_zona)

            tx_zona.text = data.nombreZonas
            tx_zona.setTextColor(Color.parseColor("#0E83C9"))
            iv_zona.setColorFilter(Color.parseColor("#0E83C9"))


            cv_zona.setBackgroundResource(R.drawable.effect_clic_mesa)

            itemView.setOnClickListener {
                cv_zona.setBackgroundResource(R.drawable.effect_clic_zona)
                Toast.makeText(view.context, "ingreso", Toast.LENGTH_SHORT).show()
                onClickListener(data)
            }
        }
    }
}