package com.example.apppedido

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.widget.Toast
import com.example.apppedido.databinding.ActivityPasscodeBinding
import kotlin.random.Random
import java.lang.*
import java.time.temporal.TemporalAccessor
import java.util.*
import kotlin.collections.ArrayList


class activityPasscode : AppCompatActivity() {

    private lateinit var binding2 : ActivityPasscodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding2.root)

        recibirDatos()
        numeroAleatorio()


    }

    private fun numeroAleatorio(){
        var grupo = listOf<Int>(1,2,3,4,5,6,7,8,9,0)

        binding2.btn01.text = grupo[0].toString()
        binding2.btn02.text = grupo[1].toString()
        binding2.btn03.text = grupo[2].toString()
        binding2.btn04.text = grupo[3].toString()
        binding2.btn05.text = grupo[4].toString()
        binding2.btn06.text = grupo[5].toString()
        binding2.btn07.text = grupo[6].toString()
        binding2.btn08.text = grupo[7].toString()
        binding2.btn09.text = grupo[8].toString()
        binding2.btn00.text = grupo[9].toString()

        binding2.btn01.setOnClickListener { numeroPresionado(binding2.btn01.text.toString()) }
        binding2.btn02.setOnClickListener { numeroPresionado(binding2.btn02.text.toString()) }
        binding2.btn03.setOnClickListener { numeroPresionado(binding2.btn03.text.toString()) }
        binding2.btn04.setOnClickListener { numeroPresionado(binding2.btn04.text.toString()) }
        binding2.btn05.setOnClickListener { numeroPresionado(binding2.btn05.text.toString()) }
        binding2.btn06.setOnClickListener { numeroPresionado(binding2.btn06.text.toString()) }
        binding2.btn07.setOnClickListener { numeroPresionado(binding2.btn07.text.toString()) }
        binding2.btn08.setOnClickListener { numeroPresionado(binding2.btn08.text.toString()) }
        binding2.btn09.setOnClickListener { numeroPresionado(binding2.btn09.text.toString()) }
        binding2.btn00.setOnClickListener { numeroPresionado(binding2.btn00.text.toString()) }

        binding2.btnLimpiar.setOnClickListener { binding2.txtCodigo.text = "" }

    }

    private fun numeroPresionado(digito:String){
        binding2.txtCodigo.text = "${binding2.txtCodigo.text}${digito}"
        validarDatos ()
    }

    fun recibirDatos(): String {
        val nombre = intent.getStringExtra("USUARIO")
        binding2.tvBienvenidaNombre.text = "BIENVENIDO $nombre"
        return nombre.toString()
    }

    fun validarDatos (){
        val Usuario = intent.getStringExtra("USUARIO")
        val Passcode = binding2.txtCodigo.text.toString()
        if (binding2.txtCodigo.length()==5){
            println("Evalua")
            if ( Usuario =="USUARIO ADMINISTRADOR" && Passcode == "12345" ){
                val intent = Intent(this, PanelPedidos::class.java)
                binding2.txtCodigo.text = ""
                startActivity(intent)
            }else{
                binding2.txtCodigo.text=""
                Toast.makeText(this, "INCORRECTO", Toast.LENGTH_SHORT).show()
            }
        }
    }

}