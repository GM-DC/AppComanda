package com.example.apppedido.infraestruture.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.R
import com.example.apppedido.domain.Model.DCMesaItem

class AdapterMesa(private val data: ArrayList<DCMesaItem>, private val onClickListener: (DCMesaItem) -> Unit): RecyclerView.Adapter<AdapterMesa.holderMesa>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderMesa {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderMesa(layoutInflater.inflate(R.layout.item_mesa,parent,false))
    }

    override fun onBindViewHolder(holder: holderMesa, position: Int) {
        holder.render(data[position],onClickListener, position)

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

    class holderMesa(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DCMesaItem, onClickListener: (DCMesaItem) -> Unit, position: Int){
            val tx_mesa = view.findViewById<TextView>(R.id.tx_mesa)
            val iv_mesa = view.findViewById<ImageView>(R.id.iv_iconMesa)
            val tx_nombreMozo = view.findViewById<TextView>(R.id.tx_nombreMozo)
            val tx_idPedido = view.findViewById<TextView>(R.id.tx_idPedido)

            var nombreMozo = ""

            if(data.NombreMozo.isNullOrEmpty()){
                nombreMozo = ""
            }else{
                nombreMozo = data.NombreMozo
            }

            if (data.estadoTrans == "L" && data.idPedido.isNullOrEmpty()){
                tx_mesa.text = "Mesa ${data.idMesa}"
                tx_nombreMozo.text = "${nombreMozo}"
                tx_idPedido.text = ""
                tx_nombreMozo.setTextColor(Color.parseColor("#0E83C9"))
                tx_mesa.setTextColor(Color.parseColor("#0E83C9"))
                iv_mesa.setColorFilter(Color.parseColor("#0E83C9"))
            }else
            if (data.estadoTrans == "O" && !data.idPedido.isNullOrEmpty()) {
                tx_mesa.text = "Mesa ${data.idMesa}"
                tx_nombreMozo.text = "${nombreMozo}"
                tx_idPedido.text = ""
                tx_nombreMozo.setTextColor(Color.parseColor("#D50000"))
                tx_mesa.setTextColor(Color.parseColor("#D50000"))
                iv_mesa.setColorFilter(Color.parseColor("#D50000"))
            }else
            if (data.estadoTrans == "O" && data.idPedido.isNullOrEmpty()) {
                tx_mesa.text = "Mesa ${data.idMesa}"
                tx_nombreMozo.text = "${nombreMozo}"
                tx_idPedido.text = ""
                tx_nombreMozo.setTextColor(Color.parseColor("#CC705D00"))
                tx_mesa.setTextColor(Color.parseColor("#CC705D00"))
                iv_mesa.setColorFilter(Color.parseColor("#CC705D00"))
            }else{
                tx_mesa.text = "Mesa ${data.idMesa}"
                tx_idPedido.text = "${data.idPedido}"
                tx_nombreMozo.text = "${nombreMozo}"
            }
        }
    }
}