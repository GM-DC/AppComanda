package com.example.apppedido.application.View

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.apppedido.DataBase.EntityZona
import com.example.apppedido.R
import com.example.apppedido.ValidarConfiguracion
import com.example.apppedido.databinding.ActivityPanelPedidosBinding
import com.example.apppedido.domain.Model.DCLoginDatosExito
import com.example.apppedido.domain.Model.DCZonaItem
import com.example.apppedido.infraestruture.network.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ActyPanelPedidos : AppCompatActivity() {

    var apiInterface: APIService? = null
    private lateinit var binding : ActivityPanelPedidosBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RECIBIR DATOS
        val recibirDatos = intent.getSerializableExtra("DatosUsuario") as DCLoginDatosExito

        binding.tvUsuario.text = recibirDatos.nombreMozo

        //ENVIAR DATOS
        val enviarDatos = Bundle()
        enviarDatos.putSerializable("DatosUsuario",recibirDatos)

        setSupportActionBar(binding.toolbarPapelprincipal)
        supportActionBar!!.title = null // Sin titulo

        //DIRECCIONA EL LUGAR DE LOS DATOS
        val framento = FrgZonaPiso()
        framento.arguments = enviarDatos

        //ABRE EL FRAMENT
        supportFragmentManager.beginTransaction().replace(R.id.frm_panel,framento).commit()

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()

    }

    // Obtiene la informacion del API Zona

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_panelprincipal, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.it_cerrarpapelprincipal -> cerrarSesionDatos()
        }
        return super.onOptionsItemSelected(item)
    }


    fun cerrarSesionDatos() {
        super.onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()

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