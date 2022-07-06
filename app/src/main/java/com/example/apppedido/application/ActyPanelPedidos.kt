package com.example.apppedido.application.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.apppedido.DataBase.ComandaDB
import com.example.apppedido.DataBase.EntityCategoria
import com.example.apppedido.DataBase.EntityZona
import com.example.apppedido.R
import com.example.apppedido.ValidarConfiguracion
import com.example.apppedido.ValidarConfiguracion.Companion.database
import com.example.apppedido.domain.Model.DCCategoriaItem
import com.example.apppedido.domain.Model.DCLoginDatosExito
import com.example.apppedido.domain.Model.DCUsuarioItem
import com.example.apppedido.domain.Model.DCZonaItem
import com.example.apppedido.infraestruture.network.APIService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActyPanelPedidos : AppCompatActivity() {

    var apiInterface: APIService? = null
    private val listaZona2 = ArrayList<DCZonaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_pedidos)

        val bt_cerrar = findViewById<Button>(R.id.bt_cerrar)

        //RECIBIR DATOS
        val recibirDatos = intent.getSerializableExtra("DATOSUSUARIO")


        //ENVIAR DATOS
        val enviarDatos = Bundle()
        enviarDatos.putSerializable("DATOUSUARIO",recibirDatos)

        println("${recibirDatos}")


        //DIRECCIONA EL LUGAR DE LOS DATOS
        val framento = FrgZonaPiso()
        framento.arguments = enviarDatos

        //ABRE EL FRAMENT
        supportFragmentManager.beginTransaction().replace(R.id.frm_panel,framento).commit()

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()

        bt_cerrar.setOnClickListener { cerrarSesionDatos() }
    }

    fun cerrarSesionDatos() {

        val intent = Intent(this@ActyPanelPedidos, ActyUsuario::class.java)
        startActivity(intent)

    }


    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) { super.onBackPressed()}
        else { supportFragmentManager.popBackStack() }
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