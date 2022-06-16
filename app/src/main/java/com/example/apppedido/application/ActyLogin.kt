package com.example.apppedido.application.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.apppedido.R
import com.example.apppedido.ValidarConfiguracion.Companion.prefs
import com.example.apppedido.application.DialogFrgConfiguracion
import com.example.apppedido.domain.Model.DataClassPedido

class ActyLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acty_login)
        val btn_configuracion = findViewById<ImageView>(R.id.btn_configuracion)

        checkDatosIniciales()
        btn_configuracion.setOnClickListener{configuracion()}
    }

    fun checkDatosIniciales() {

    }

    fun configuracion(){
        var dialog = DialogFrgConfiguracion()
        dialog.show(supportFragmentManager,"customDialog")
    }



}


/*
* //***********  Alerta de Dialogo  ***********
    //val builder = activity?.let { AlertDialog.Builder(it) }
    val builder = AlertDialog.Builder(this)
    val vista = layoutInflater.inflate(R.layout.dialogue_configuracion,null)


    builder.setView(vista)

    val dialog = builder.create()
    dialog.show()

    //***********Declara elementos *****************
    val bt_guardarDetalle = vista.findViewById<Button>(R.id.bt_guardarConfiguracion)


    //*********** BOTON GUARDAR DEL DIALOGO ********
    bt_guardarDetalle.setOnClickListener {
        //***********  Alerta de Dialogo  ***********
        val vista = layoutInflater.inflate(R.layout.dialogue_configuracion,null)
        builder.setView(vista)
        val dialog = builder.create()
        dialog.show()

        //*********** BOTON GUARDAR DEL DIALOGO ********
        bt_guardarDetalle.setOnClickListener {
            dialog.hide()
        }
    }
*
* */