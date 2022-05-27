package com.example.apppedido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.databinding.ActivityPanelPedidoBinding


class panelPedido : AppCompatActivity() {

    private lateinit var comp : ActivityPanelPedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comp = ActivityPanelPedidoBinding.inflate(layoutInflater)
        setContentView(comp.root)
        initZona()
        initMesa()
        initCategoria()
        initPlato()
        initPedido()
    }



    fun initZona(){
        val rv_zona = findViewById<RecyclerView>(R.id.rv_zona)
        rv_zona.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        val adapter = AdapterZona(listaZona)
        rv_zona.adapter = adapter
    }

    fun initMesa(){
        val rv_mesa = findViewById<RecyclerView>(R.id.rv_mesa)
        rv_mesa.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        val adapter = AdapterMesa(listaMesa)
        rv_mesa.adapter = adapter
    }

    fun initCategoria(){
        val rv_categoria = findViewById<RecyclerView>(R.id.rv_categoria)
        rv_categoria.layoutManager = GridLayoutManager(this,2,RecyclerView.HORIZONTAL,false)
        val adapter = AdapterCategoria(listaCategoria)
        rv_categoria.adapter = adapter
    }

    fun initPlato(){
        val rv_plato = findViewById<RecyclerView>(R.id.rv_platillo)
        rv_plato.layoutManager = GridLayoutManager(this,3,RecyclerView.HORIZONTAL,false)
        val adapter = AdapterPlato(listaPlato) { dataclassPlato -> onItemDatosPedido(dataclassPlato) }
        rv_plato.adapter = adapter
    }

    fun initPedido(){
        val rv_pedido = findViewById<RecyclerView>(R.id.rv_pedido)
        rv_pedido.layoutManager = LinearLayoutManager(this)
        agregarPlatos()
        val adapter = AdapterPedido(listaPedido, {dataclassPedido -> onIntemDatosPlatos(dataclassPedido)})
        rv_pedido.adapter = adapter

    }

    fun onItemDatosPedido(dataClassPlato: DataClassPlato){
        val rv_pedido = findViewById<RecyclerView>(R.id.rv_pedido)
        val datos = dataClassPlato.copy()
        Toast.makeText(this, dataClassPlato.name, Toast.LENGTH_SHORT).show()

        //-------------Evalua POSICION Y ACCION DE AGREGAR-------------------
        //println("------- Evalua POSICION Y ACCION DE AGREGAR-------------")
        var action = 0
        var pos = -1
        for (i in listaPedido.indices){
            if(listaPedido[i].namePlato==datos.name){
                action += 1
            }
            if (action == 1){
                pos = i
                println("posicion: $pos")
                break
            }
        }
        //println("-----------------  FIN  --------------------")
        //--------------------------- FIN ------------------------------------


        //----------------  AGREGA O AUMENTA LA CANTIDAD -------------------
        if (action == 0){
            listaPedido.add(DataClassPedido(1,dataClassPlato.name,dataClassPlato.categoria,dataClassPlato.precio))
            rv_pedido.adapter?.notifyDataSetChanged()
            rv_pedido.scrollToPosition(listaPedido.size-1)
        }else{
            val lt = listaPedido.get(pos)
            var cantidad = lt.cantidad+1
            var precioTotal = dataClassPlato.precio*cantidad
            listaPedido.set(pos, DataClassPedido(cantidad,lt.namePlato,lt.categoria,precioTotal))
            rv_pedido.adapter?.notifyDataSetChanged()
        }
        //-------------------------------------------------------------------

        //------------------  SUMA DE PRECIO DE LA LISTA---------------
        var cantidad:Float = 0f
        for (i in listaPedido.indices){
            cantidad = cantidad + listaPedido[i].precio
        }
        val tv_PTotal = findViewById<TextView>(R.id.tv_PTotal)
        tv_PTotal.text = "S/. ${(Math.round(cantidad) * 100.0 / 100.0).toString()}"
        //-------------------------------------------------------------
    }

    fun onIntemDatosPlatos(dataclassPedido:DataClassPedido){
        val rv_pedido = findViewById<RecyclerView>(R.id.rv_pedido)
        val bt_eliminar = findViewById<Button>(R.id.bt_disminuir)
        val bt_aumentar = findViewById<Button>(R.id.bt_aumentar)
        val bt_detalle = findViewById<Button>(R.id.bt_detalle)
        val datos = dataclassPedido.copy()
        Toast.makeText(this, dataclassPedido.namePlato, Toast.LENGTH_SHORT).show()

        //-------------Posicion--------------------------------------
        var index = -1
        for (i in listaPedido.indices) {
            if (listaPedido[i].namePlato == datos.namePlato){
                println("Bucle: ${listaPedido[i].namePlato} == ${datos.namePlato}")
                index = i
                break
            }
        }
        //----------------------------------------------------------

        //-----------BOTON ELIMINAR----------------
        bt_eliminar.setOnClickListener {
            println("El index: $index")
            println("Eliminar condicion: ${datos.namePlato} == ${listaPedido.get(index).namePlato}")

            if (datos.namePlato==listaPedido.get(index).namePlato && listaPedido.get(index).cantidad>1){
                val lt = listaPedido.get(index)
                var cantidad = lt.cantidad
                var precioTotal = lt.precio - (lt.precio/lt.cantidad)
                listaPedido.set(index, DataClassPedido(cantidad-1,lt.namePlato,lt.categoria,precioTotal))
                rv_pedido.adapter?.notifyDataSetChanged()
            }else{
                listaPedido.remove(datos)
                rv_pedido.adapter?.notifyDataSetChanged()
            }

            //------------------  SUMA DE PRECIO DE LA LISTA---------------
            var cantidad2:Float = 0f
            for (i in listaPedido.indices){
                cantidad2 = cantidad2 + listaPedido[i].precio
            }
            val tv_PTotal = findViewById<TextView>(R.id.tv_PTotal)
            tv_PTotal.text = "S/. ${(Math.round(cantidad2) * 100.0 / 100.0).toString()}"
            //-------------------------------------------------------------

        }

        //-----------------------------------------

        //-----------BOTON AUMENTAR---------------
        bt_aumentar.setOnClickListener {
            val lt = listaPedido.get(index)
            var cantidad = lt.cantidad+1
            var precioTotal = (lt.precio/lt.cantidad)*cantidad
            listaPedido.set(index, DataClassPedido(cantidad,lt.namePlato,lt.categoria,precioTotal))
            rv_pedido.adapter?.notifyDataSetChanged()

            //------------------  SUMA DE PRECIO DE LA LISTA---------------
            var cantidad2:Float = 0f
            for (i in listaPedido.indices){
                cantidad2 = cantidad2 + listaPedido[i].precio
            }
            val tv_PTotal = findViewById<TextView>(R.id.tv_PTotal)
            tv_PTotal.text = "S/. ${(Math.round(cantidad2) * 100.0 / 100.0).toString()}"
            //-------------------------------------------------------------

        }

        //-----------BOTON DETALLE-----------------
        bt_detalle.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val vista = layoutInflater.inflate(R.layout.dialogue_detalle,null)

            builder.setView(vista)

            val dialog = builder.create()
            dialog.show()


            val nota = vista.findViewById<EditText>(R.id.et_detalle).text
            val bt_guardarDetalle = vista.findViewById<Button>(R.id.bt_guardarDetalle)


            bt_guardarDetalle.setOnClickListener {
                val detalle = nota
                println("GUARDADO: $detalle")
                dialog.hide()
            }

        }

    }




    val listaZona = listOf<DataClassZona>(
        DataClassZona("1","Zona01"),
        DataClassZona("2","Zona02"),
        DataClassZona("3","Zona03"),
        DataClassZona("4","Zona04"),
        DataClassZona("5","Zona05"),
        DataClassZona("6","Zona06"),
        DataClassZona("7","Zona07"),
        DataClassZona("8","Zona08"),
        DataClassZona("9","Zona09"),
        DataClassZona("10","Zona10"),
        DataClassZona("11","Zona11"),
        DataClassZona("10","Zona12"),
        DataClassZona("11","Zona13")
    )

    val listaMesa = listOf<DataClassMesa>(
        DataClassMesa("1","M 01"),
        DataClassMesa("2","M 02"),
        DataClassMesa("3","M 03"),
        DataClassMesa("4","M 04"),
        DataClassMesa("5","M 05"),
        DataClassMesa("6","M 06"),
        DataClassMesa("7","M 07"),
        DataClassMesa("8","M 08"),
        DataClassMesa("9","M 09"),
        DataClassMesa("10","M 10"),
        DataClassMesa("11","M 11"),
        DataClassMesa("10","M 12"),
        DataClassMesa("11","M 13")
    )

    val listaCategoria = listOf<DataClassCategoria>(
        DataClassCategoria("1","Pizzas"),
        DataClassCategoria("2","Pollos"),
        DataClassCategoria("3","Postres"),
        DataClassCategoria("4","Sopas"),
        DataClassCategoria("5","Vinos"),
        DataClassCategoria("6","Promociones"),
        DataClassCategoria("7","Parrilla"),
        DataClassCategoria("8","Gaseora"),
        DataClassCategoria("9","Guardicion"),
        DataClassCategoria("10","Entrada"),
        DataClassCategoria("11","Menu"),
        DataClassCategoria("10","Oferta"),
        DataClassCategoria("11","Promo")
    )

    val listaPlato = listOf<DataClassPlato>(
        DataClassPlato("1","Salchipapa","Pollo",5.01f),
        DataClassPlato("2","Pequeños","Pollo",7.30f),
        DataClassPlato("3","Alitas","Pollo",4.56f),
        DataClassPlato("4","Broschetas","Pollo",6.99f),
        DataClassPlato("5","Anticucho","Pollo",8.69f),
        DataClassPlato("6","Piqueo","Pollo",25.35f),
        DataClassPlato("7","Porcino","Pollo",25.35f),
        DataClassPlato("8","Parrila","Pollo",25.35f),
        DataClassPlato("9","Coca-Cola","Pollo",25.35f),
        DataClassPlato("10","Inka-Cola","Pollo",25.35f),
        DataClassPlato("11","Pastel","Pollo",25.35f),
        DataClassPlato("10","Torta","Pollo",25.35f),
        DataClassPlato("11","Pan","Pollo",25.35f)
    )

    val listaPedido = ArrayList<DataClassPedido>()

    fun agregarPlatos(){

        //listaPedido.add(DataClassPedido(1,"Salchipapa","Pollos",25.3f),)
        //listaPedido.add(DataClassPedido(1,"Pollito","Pollos",15.5f))
    }

}