package com.example.apppedido

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import java.lang.StringBuilder
import java.math.BigDecimal
import java.text.DecimalFormat

class AdapterPedido(private val data: List<DataClassPedido>,private val onClickListener: (DataClassPedido) -> Unit): RecyclerView.Adapter<AdapterPedido.holderPedido>() {

    val listaPedido = ArrayList<DataClassPedido>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderPedido {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderPedido(layoutInflater.inflate(R.layout.item_pedido,parent,false))
    }

    override fun onBindViewHolder(holder: holderPedido, position: Int) {
        holder.render(data[position],onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size

    }

    inner class holderPedido(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DataClassPedido, onClickListener: (DataClassPedido) -> Unit){

            //******* DECLARANDO COMPONENTES *****
            val tv_nombrePlato = view.findViewById<TextView>(R.id.tv_nombrePlato)
            val tv_precioTotal = view.findViewById<TextView>(R.id.tv_precioTotal)
            val bt_aumentar = view.findViewById<TextView>(R.id.bt_aumentar)
            val bt_disminuir = view.findViewById<TextView>(R.id.bt_disminuir)
            val tv_cantidad = view.findViewById<TextView>(R.id.tv_cantidad)

            val lbl_preciototal = view.findViewById<TextView>(R.id.tv_PTotal)


            //************ ASIGNANDO COMPONENTES ********
            tv_cantidad.text = data.cantidad.toString()
            tv_nombrePlato.text = data.namePlato

            itemView.setOnClickListener { onClickListener(data) }

            bt_aumentar.setOnClickListener{
                data.cantidad = data.cantidad+1
                data.precioTotal = data.precio*data.cantidad.toBigDecimal()
                tv_cantidad.text = data.cantidad.toString()
                tv_precioTotal.text = data.precioTotal.toString()
            }

            bt_disminuir.setOnClickListener{
                if (data.cantidad > 1 ){
                    data.cantidad = data.cantidad-1
                    data.precioTotal = data.precio*data.cantidad.toBigDecimal()
                    tv_cantidad.text = data.cantidad.toString()
                    tv_precioTotal.text = data.precioTotal.toString()

                }
            }


        }
    }
    fun TotalSumaDos (x: BigDecimal, y:BigDecimal): String {
        return (x*y).toString();
    }





}

