package com.example.apppedido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        val adapter = AdapterPlato(listaPlato)
        rv_plato.adapter = adapter
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
        DataClassPlato("1","Salchipapa"),
        DataClassPlato("2","Pequeños"),
        DataClassPlato("3","Alitas"),
        DataClassPlato("4","Broschetas"),
        DataClassPlato("5","Anticucho"),
        DataClassPlato("6","Piqueo"),
        DataClassPlato("7","Porcino"),
        DataClassPlato("8","Parrila"),
        DataClassPlato("9","Coca-Cola"),
        DataClassPlato("10","Inka-Cola"),
        DataClassPlato("11","Pastel"),
        DataClassPlato("10","Torta"),
        DataClassPlato("11","Pan")
    )
}