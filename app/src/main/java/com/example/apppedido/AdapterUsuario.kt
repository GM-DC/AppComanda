package com.example.apppedido

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterUsuario(val usuario: MutableList<DCUsuarioItem>, private val onClickListener: (DCUsuarioItem) -> Unit):RecyclerView.Adapter<AdapterUsuario.holderUsuario>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderUsuario {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderUsuario(layoutInflater.inflate(R.layout.item_usuarios,parent,false))
    }

    override fun onBindViewHolder(holder: holderUsuario, position: Int) {
        holder.render(usuario[position],onClickListener)
    }

    override fun getItemCount(): Int {
        return usuario.size
    }


    class holderUsuario(private val view: View):RecyclerView.ViewHolder(view){

        fun render (usuario: DCUsuarioItem,onClickListener: (DCUsuarioItem) -> Unit){
            val tv_usuario = view.findViewById<TextView>(R.id.tx_Nombre)
            val cv_usuario = view.findViewById<ImageView>(R.id.iv_iconUsuario)

            tv_usuario.text = usuario.nombre
            tv_usuario.setTextColor(Color.parseColor("#0E83C9"))
            cv_usuario.setColorFilter(Color.parseColor("#0E83C9"))

            itemView.setOnClickListener { onClickListener(usuario) }
        }
    }
}