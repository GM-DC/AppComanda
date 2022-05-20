package com.example.apppedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class AdapterUsuario(val usuario:List<DataClassUsuario>):RecyclerView.Adapter<AdapterUsuario.holderUsuario>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderUsuario {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderUsuario(layoutInflater.inflate(R.layout.item_usuarios,parent,false))
    }

    override fun onBindViewHolder(holder: holderUsuario, position: Int) {
        holder.render(usuario[position])
    }

    override fun getItemCount(): Int {
        return usuario.size
    }


    class holderUsuario(val view: View):RecyclerView.ViewHolder(view){
        fun render (usuario: DataClassUsuario){
            val btn_usuario = view.findViewById<Button>(R.id.tx_Nombre)
            btn_usuario.text = usuario.name
        }
    }




}