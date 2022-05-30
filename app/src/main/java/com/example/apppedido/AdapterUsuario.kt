package com.example.apppedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterUsuario(val usuario: List<DataClassUsuario>,private val onClickListener: (DataClassUsuario) -> Unit):RecyclerView.Adapter<AdapterUsuario.holderUsuario>(){


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
        fun render (usuario: DataClassUsuario,onClickListener: (DataClassUsuario) -> Unit){
            val btn_usuario = view.findViewById<TextView>(R.id.tx_Nombre)
            btn_usuario.text = usuario.name
            itemView.setOnClickListener { onClickListener(usuario) }
        }
    }
}