package com.example.apppedido

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PanelPedidos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_pedidos)

        //RECIBIR DATOS
        val recibirDatos = intent.getSerializableExtra("DATOSUSUARIO")
        println("Datos 444444444444444 : ${recibirDatos}")

        //ENVIAR DATOS
        val enviarDatos = Bundle()
        enviarDatos.putSerializable("DATOUSUARIO",recibirDatos)

        //DIRECCIONA EL LUGAR DE LOS DATOS
        val framento = FrgZonaPiso()
        framento.arguments = enviarDatos

        //ABRE EL FRAMENT
        supportFragmentManager.beginTransaction().replace(R.id.frm_panel,framento).commit()


    }
}