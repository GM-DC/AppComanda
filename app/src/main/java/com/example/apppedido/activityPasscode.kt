package com.example.apppedido

import DCLoginDatosExito
import DCLoginUser
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.widget.Toast
import com.example.apppedido.databinding.ActivityPasscodeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random
import java.lang.*
import java.time.temporal.TemporalAccessor
import java.util.*
import kotlin.collections.ArrayList


class activityPasscode : AppCompatActivity()  {

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
        val nombre = intent.getStringExtra("USUARIOMOZO")
        binding2.tvBienvenidaNombre.text = "BIENVENIDO $nombre"
        return nombre.toString()
    }

    fun validarDatos (){

        val idMozo = intent.getStringExtra("IDMOZO")
        val passMozo = binding2.txtCodigo.text.toString()

        if (binding2.txtCodigo.length()==6){
            println("Llega aca 1")
            getDataLogin(idMozo!!,passMozo)
            binding2.txtCodigo.text = ""
        }
    }


    // Obtiene la informacion del API Mesa
    private fun getDataLogin(usuarioMozo:String,passMozo:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = getRetrofit().checkLoginComanda(DCLoginUser("$usuarioMozo","$passMozo"))
            runOnUiThread {
                println("Llega aca 2")
                if(response.isSuccessful){
                    val intent = Intent(applicationContext, PanelPedidos::class.java)

    //                DCLoginDatosExito(). = response.body()!!
                    val enviarDatosMozo = Bundle()
                    
                    val nameMozo = response.body()?.nameMozo
                    intent.putExtra("NAMEMOZO",nameMozo)

                    //binding2.txtCodigo.text = ""
                    startActivity(intent)
                }else{
                    println("falla")
                    //binding2.txtCodigo.text=""
                    //Toast.makeText(applicationContext, "INCORRECTO", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





    // Devuelve un Retrofit
    fun getRetrofit(): APIService {





        val retrofit = Retrofit.Builder()
            .baseUrl("http://heyeldevs-001-site1.gtempurl.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return  retrofit.create(APIService::class.java)
    }



}

