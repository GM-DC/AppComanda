package com.example.apppedido.application.View

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.apppedido.R

class ActyPanelPedidos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_pedidos)

        //RECIBIR DATOS
        val recibirDatos = intent.getSerializableExtra("DATOSUSUARIO")

        //ENVIAR DATOS
        val enviarDatos = Bundle()
        enviarDatos.putSerializable("DATOUSUARIO",recibirDatos)

        //DIRECCIONA EL LUGAR DE LOS DATOS
        val framento = FrgZonaPiso()
        framento.arguments = enviarDatos

        //ABRE EL FRAMENT
        supportFragmentManager.beginTransaction().replace(R.id.frm_panel,framento).commit()

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        println(count)

        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun desaparecerBarraNavegacion() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}