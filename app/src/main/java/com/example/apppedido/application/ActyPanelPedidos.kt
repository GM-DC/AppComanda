package com.example.apppedido.application.View

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.apppedido.R
import com.example.apppedido.databinding.ActivityPanelPedidosBinding
import com.example.apppedido.domain.Model.DCZonaItem
import com.example.apppedido.infraestruture.network.APIService


class ActyPanelPedidos : AppCompatActivity() {

    var apiInterface: APIService? = null
    private val listaZona2 = ArrayList<DCZonaItem>()
    private lateinit var binding : ActivityPanelPedidosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RECIBIR DATOS
        val recibirDatos = intent.getSerializableExtra("DATOSUSUARIO")

        //ENVIAR DATOS
        val enviarDatos = Bundle()
        enviarDatos.putSerializable("DATOUSUARIO",recibirDatos)

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
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) { super.onBackPressed()}
        else { supportFragmentManager.popBackStack() }
        desaparecerBarraNavegacion()
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