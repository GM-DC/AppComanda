package com.example.apppedido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        rv_categoria.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        val adapter = AdapterCategoria(listaCategoria)
        rv_categoria.adapter = adapter
    }

    val listaZona = listOf<DataClassZona>(
        DataClassZona("1","Zona 01"),
        DataClassZona("2","Zona 02"),
        DataClassZona("3","Zona 03"),
        DataClassZona("4","Zona 04"),
        DataClassZona("5","Zona 05"),
        DataClassZona("6","Zona 06"),
        DataClassZona("7","Zona 07"),
        DataClassZona("8","Zona 08"),
        DataClassZona("9","Zona 09"),
        DataClassZona("10","Zona 10"),
        DataClassZona("11","Zona 11"),
        DataClassZona("10","Zona 12"),
        DataClassZona("11","Zona 13")
    )

    val listaMesa = listOf<DataClassMesa>(
        DataClassMesa("1","Mesa 01"),
        DataClassMesa("2","Mesa 02"),
        DataClassMesa("3","Mesa 03"),
        DataClassMesa("4","Mesa 04"),
        DataClassMesa("5","Mesa 05"),
        DataClassMesa("6","Mesa 06"),
        DataClassMesa("7","Mesa 07"),
        DataClassMesa("8","Mesa 08"),
        DataClassMesa("9","Mesa 09"),
        DataClassMesa("10","Mesa 10"),
        DataClassMesa("11","Mesa 11"),
        DataClassMesa("10","Mesa 12"),
        DataClassMesa("11","Mesa 13")
    )

    val listaCategoria = listOf<DataClassCategoria>(
        DataClassCategoria("1","Mesa 01"),
        DataClassCategoria("2","Mesa 02"),
        DataClassCategoria("3","Mesa 03"),
        DataClassCategoria("4","Mesa 04"),
        DataClassCategoria("5","Mesa 05"),
        DataClassCategoria("6","Mesa 06"),
        DataClassCategoria("7","Mesa 07"),
        DataClassCategoria("8","Mesa 08"),
        DataClassCategoria("9","Mesa 09"),
        DataClassCategoria("10","Mesa 10"),
        DataClassCategoria("11","Mesa 11"),
        DataClassCategoria("10","Mesa 12"),
        DataClassCategoria("11","Mesa 13")
    )

}