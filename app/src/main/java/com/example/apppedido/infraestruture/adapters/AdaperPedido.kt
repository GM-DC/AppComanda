package com.example.apppedido.infraestruture.adapters
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.R
import com.example.apppedido.domain.Model.DataClassPedido

class AdapterPedido(private val data: ArrayList<DataClassPedido>, private val onClickListener: (DataClassPedido) -> Unit): RecyclerView.Adapter<AdapterPedido.holderPedido>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderPedido {
        val layoutInflater = LayoutInflater.from(parent.context)
        return holderPedido(layoutInflater.inflate(R.layout.item_pedido,parent,false))
    }

    override fun onBindViewHolder(holder: holderPedido, position: Int) {
        holder.render(data[position],onClickListener)

        holder.itemView.setBackgroundResource(R.drawable.effect_clic_mesa) //negro

        if (selectedPosition === position) {
            holder.itemView.setBackgroundResource(R.drawable.effect_clic_pedido) //banco
            selectedPosition = -1
        }else {
            holder.itemView.setBackgroundResource(R.drawable.effect_clic_mesa) //negro
        }

        holder.itemView.setOnClickListener {
            onClickListener(data[position]
            )
            selectedPosition = position
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return data.size

    }

    class holderPedido(private val view: View): RecyclerView.ViewHolder(view){
        fun render (data: DataClassPedido, onClickListener: (DataClassPedido) -> Unit) {

            //******* DECLARANDO COMPONENTES *****
            val tv_nombrePlato = view.findViewById<TextView>(R.id.tv_nombrePlato)
            val tv_cantidad = view.findViewById<TextView>(R.id.tv_cantidad)
            val tv_precio = view.findViewById<TextView>(R.id.tv_precio)
            val tv_precioTotal = view.findViewById<TextView>(R.id.tv_precioTotal)
            val precio = view.findViewById<TextView>(R.id.precio)
            val cantidad = view.findViewById<TextView>(R.id.cantidad)
            val total = view.findViewById<TextView>(R.id.total)


            if(data.estadoPedido=="PENDIENTE"){
                tv_nombrePlato.setTextColor(Color.parseColor("#11468F"))
                tv_precio.setTextColor(Color.parseColor("#11468F"))
                tv_precioTotal.setTextColor(Color.parseColor("#11468F"))
                tv_cantidad.setTextColor(Color.parseColor("#11468F"))
                precio.setTextColor(Color.parseColor("#11468F"))
                cantidad.setTextColor(Color.parseColor("#11468F"))
                total.setTextColor(Color.parseColor("#11468F"))


            }else{
                tv_nombrePlato.setTextColor(Color.parseColor("#DA1212"))
                tv_precio.setTextColor(Color.parseColor("#DA1212"))
                tv_precioTotal.setTextColor(Color.parseColor("#DA1212"))
                tv_cantidad.setTextColor(Color.parseColor("#DA1212"))
                precio.setTextColor(Color.parseColor("#DA1212"))
                cantidad.setTextColor(Color.parseColor("#DA1212"))
                total.setTextColor(Color.parseColor("#DA1212"))
            }

            //************ ASIGNANDO COMPONENTES ********
            tv_cantidad.text = "${data.cantidad}"

            tv_nombrePlato.text = data.namePlato
            tv_precio.text = "${data.precio}"
            data.precioTotal = data.precio*data.cantidad
            tv_precioTotal.text = "${data.precioTotal}"

        }


    }



}

