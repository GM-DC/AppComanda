package com.example.apppedido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.apppedido.databinding.ActivityPanelPedidoBinding
import com.example.apppedido.databinding.ActivityPasscodeBinding


class panelPedido : AppCompatActivity() {

    private lateinit var comp : ActivityPanelPedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comp = ActivityPanelPedidoBinding.inflate(layoutInflater)
        setContentView(comp.root)
    }

}