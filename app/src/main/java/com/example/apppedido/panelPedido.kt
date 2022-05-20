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

    }

    fun initZona(){
        val rv_zona = findViewById<RecyclerView>(R.id.rv_zona)
        rv_zona.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        val adapter = AdapterZona(listaZona)
        rv_zona.adapter = adapter
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

}