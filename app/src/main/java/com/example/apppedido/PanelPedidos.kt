package com.example.apppedido

import android.os.Bundle
import android.view.View
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

        hideSystemUI()

        //ABRE EL FRAMENT
        supportFragmentManager.beginTransaction().replace(R.id.frm_panel,framento).commit()


    }


    private fun hideSystemUI() {
        val decorView: View = window.decorView
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }
}