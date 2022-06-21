package com.example.apppedido.application.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.apppedido.R
import com.example.apppedido.ValidarConfiguracion.Companion.prefs

class ActyLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acty_login)


        dataDialogo()
        checkValores()
    }

    private fun checkValores() {
        if (prefs.getUsuario().isNotEmpty() || prefs.getContrasena().isNotEmpty() || prefs.getDominio().isNotEmpty() || prefs.getPuerto().isNotEmpty()){
            goToActyUsuario()
        }
    }

    fun dataDialogo() {
        val btn_configuracion = findViewById<ImageView>(R.id.btn_configuracion)
        val bt_conectar = findViewById<Button>(R.id.bt_conectar)
        val et_usuario = findViewById<EditText>(R.id.et_usuario)
        val et_contrasena = findViewById<EditText>(R.id.et_contrasena)
        var Confirmado = false

        btn_configuracion.setOnClickListener{
                //Asignar Valores
                val builder = AlertDialog.Builder(this@ActyLogin)
                val view = layoutInflater.inflate(R.layout.dialogue_configuracion,null)

                //Pasando la vista al builder
                builder.setView(view)

                //Creando dialog
                val dialog = builder.create()
                dialog.show()

                val et_ip = view.findViewById<EditText>(R.id.et_ip)
                val et_port = view.findViewById<EditText>(R.id.et_port)
                val bt_guardarConfiguracion = view.findViewById<Button>(R.id.bt_guardarConfiguracion)

                bt_guardarConfiguracion.setOnClickListener {
                    if (et_ip?.text!!.toString().equals("") || et_port.text!!.toString().equals("")){
                        Confirmado = false
                        Toast.makeText(this, "INGRESAR CONFIGURACIONES", Toast.LENGTH_SHORT).show()
                    }else{
                        prefs.saveDominio(et_ip.text.toString())
                        prefs.savePuerto(et_port.text.toString())
                        Confirmado = true

                        dialog.hide()
                    }
                }
        }


        bt_conectar.setOnClickListener {
            if (et_usuario?.text!!.toString().equals("") || et_contrasena.text!!.toString().equals("")){
                println("FALTA INGRESAR USUARIO O CONTRASEÑA")
                Toast.makeText(this, "FALTA INGRESAR USUARIO O CONTRASEÑA", Toast.LENGTH_SHORT).show()
            }else{
                if (Confirmado==false){
                    println("FALTA CONFIGURAR IP/HOST O PUERTO")
                    Toast.makeText(this, "FALTA CONFIGURAR IP/HOST O PUERTO", Toast.LENGTH_SHORT).show()
                }else{
                    prefs.saveUsuario(et_usuario.text.toString())
                    prefs.saveContrasena(et_contrasena.text.toString())
                    goToActyUsuario()
                }
            }
        }
    }

    fun goToActyUsuario(){
        startActivity(Intent(this, ActyUsuario::class.java))
    }



}
