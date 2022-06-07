package com.example.apppedido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PanelPedidos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_pedidos)

        supportFragmentManager.beginTransaction().replace(R.id.frm_panel,FrgZonaPiso()).commit()
    }
}