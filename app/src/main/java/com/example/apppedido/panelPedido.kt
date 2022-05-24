package com.example.apppedido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

    fun onItemDatosPedido(dataClassPedido: DataClassPlato){
        Toast.makeText(this, dataClassPedido.name, Toast.LENGTH_SHORT).show()

        listaPedido.add(DataClassPedido(1,dataClassPedido.name,dataClassPedido.categoria,dataClassPedido.precio))

        val rv_pedido = findViewById<RecyclerView>(R.id.rv_pedido)
        rv_pedido.adapter?.notifyDataSetChanged()
        rv_pedido.scrollToPosition(listaPedido.size-1)
    }

    fun onIntemDatosPlatos(dataclassPedido:DataClassPedido){
        val rv_pedido = findViewById<RecyclerView>(R.id.rv_pedido)
        val bt_eliminar = findViewById<Button>(R.id.bt_disminuir)
        val bt_aumentar = findViewById<Button>(R.id.bt_aumentar)

        Toast.makeText(this, dataclassPedido.namePlato, Toast.LENGTH_SHORT).show()

        val datos = dataclassPedido.copy()
        //println(datos)

        bt_eliminar.setOnClickListener {

            if (datos!=null){
                listaPedido.remove(datos)
                rv_pedido.adapter?.notifyDataSetChanged()
            }else{
                println("No hay nada")
                Toast.makeText(this, "No hay datos", Toast.LENGTH_SHORT).show()
            }
        }

        bt_aumentar.setOnClickListener {
        /*


            if (datos!=null){

                val index:Int = listaPedido.indexOf(datos)

                println(listaPedido.indexOf(datos))
                println(DataClassPedido((datos.cantidad.toInt()+1).toString(),datos.namePlato,datos.categoria,datos.precio))
                //rv_pedido.adapter?.notifyDataSetChanged()

                println(listaPedido.set(index,DataClassPedido((datos.cantidad.toInt()+1).toString(),datos.namePlato,datos.categoria,datos.precio)))


                Toast.makeText(this, "si hay datos", Toast.LENGTH_SHORT).show()
            }else{

                println("No hay nada")
                Toast.makeText(this, "No hay datos", Toast.LENGTH_SHORT).show()
            }
            */
        }


        //listaPedido.remove(datos)
        //rv_pedido.adapter?.notifyDataSetChanged()


        /*

        //INICIO -- Premite sabes ----------------
        var posi = -1
        for(i in 1..listaPedido.size){
            println(i)
            if (listaPedido.get(i).namePlato.equals(dataclassPedido.namePlato)){
                println(listaPedido.get(i).namePlato.equals(dataclassPedido.namePlato))
                posi = i
                println(i)
            }
        }
        //FIN---------------------------------------


        if (posi!=-1){
            listaPedido.remove(datos)
            val rv_pedido = findViewById<RecyclerView>(R.id.rv_pedido)
            rv_pedido.adapter?.notifyDataSetChanged()
        }else{
            Toast.makeText(this, "No exite", Toast.LENGTH_SHORT).show()
        }*/

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
        DataClassPlato("1","Salchipapa","Pollo",25.3f),
        DataClassPlato("2","Pequeños","Pollo",25.3f),
        DataClassPlato("3","Alitas","Pollo",25.3f),
        DataClassPlato("4","Broschetas","Pollo",25.3f),
        DataClassPlato("5","Anticucho","Pollo",25.3f),
        DataClassPlato("6","Piqueo","Pollo",25.3f),
        DataClassPlato("7","Porcino","Pollo",25.3f),
        DataClassPlato("8","Parrila","Pollo",25.3f),
        DataClassPlato("9","Coca-Cola","Pollo",25.3f),
        DataClassPlato("10","Inka-Cola","Pollo",25.3f),
        DataClassPlato("11","Pastel","Pollo",25.3f),
        DataClassPlato("10","Torta","Pollo",25.3f),
        DataClassPlato("11","Pan","Pollo",25.3f)
    )

    val listaPedido = ArrayList<DataClassPedido>()

    fun agregarPlatos(){
        listaPedido.add(DataClassPedido(1,"Salchipapa","Pollos",25.3f),)
        listaPedido.add(DataClassPedido(1,"Pollito","Pollos",15.5f))
    }


    /*
    listaPedido.add(DataClassPedido("1","Salchipapa","Pollos",25.3f)),
        DataClassPedido("2","Salchipapa","Pollos",25.3f),
        DataClassPedido("3","Salchipapa","Pollos",25.3f),
        DataClassPedido("4","Salchipapa","Pollos",25.3f),
        DataClassPedido("5","Salchipapa","Pollos",25.3f),
        DataClassPedido("6","Salchipapa","Pollos",25.3f),
        DataClassPedido("7","Salchipapa","Pollos",25.3f),
        DataClassPedido("8","Salchipapa","Pollos",25.3f),
        DataClassPedido("9","Salchipapa","Pollos",25.3f),
        DataClassPedido("10","Salchipapa","Pollos",25.3f),
        DataClassPedido("11","Salchipapa","Pollos",25.3f),
        DataClassPedido("12","Salchipapa","Pollos",25.3f),
        DataClassPedido("13","Salchipapa","Pollos",25.3f)
    */




}