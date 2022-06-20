package com.example.apppedido.infraestruture.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.R
import com.example.apppedido.domain.Model.DCCategoriaItem
import kotlin.random.Random
import kotlin.random.Random.Default.nextFloat

class AdapterCategoria(private val data: List<DCCategoriaItem>, private val onClickListener: (DCCategoriaItem) -> Unit): RecyclerView.Adapter<AdapterCategoria.holderCategoria>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderCategoria {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderCategoria(layoutInflater.inflate(R.layout.item_categoria,parent,false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: holderCategoria, position: Int) {
        holder.render(data[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class holderCategoria(private val view: View): RecyclerView.ViewHolder(view){
        @RequiresApi(Build.VERSION_CODES.O)
        fun render (data: DCCategoriaItem, onClickListener: (DCCategoriaItem) -> Unit){
            val tx_categoria = view.findViewById<TextView>(R.id.tx_categoria)
            val iv_categoria = view.findViewById<ImageView>(R.id.iv_iconCategoria)

            val r = nextFloat()
            val g = nextFloat()
            val b = nextFloat()
            val randomColor = Color.rgb(r,g,b)

            tx_categoria.text = data.nameCategoria
            tx_categoria.setTextColor(randomColor)
            iv_categoria.setColorFilter(randomColor)

            itemView.setOnClickListener { onClickListener(data) }

        }
    }
}