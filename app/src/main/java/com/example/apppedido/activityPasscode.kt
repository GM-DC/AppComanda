package com.example.apppedido

import DCLoginUser
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apppedido.databinding.ActivityPasscodeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random


class activityPasscode : AppCompatActivity()  {

    private lateinit var binding2 : ActivityPasscodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding2.root)

        recibirDatos()
        numeroAleatorio()
    }

    //NUMERO ALEATORIO
    private fun numeroAleatorio(){

        val positions = IntArray(10)
        val rdr = java.util.Random()
        for (i in 0..9) {
            var toPos: Int = rdr.nextInt(10 - 1 + 1) + 1
            var shouldContinue: Boolean = positions.contains(toPos)
            while (shouldContinue) {
                toPos = rdr.nextInt(10 - 1 + 1) + 1
                shouldContinue = positions.contains(toPos)
            }
            when (toPos) {
                1 -> binding2.btn01.text = i.toString()
                2 -> binding2.btn02.text = i.toString()
                3 -> binding2.btn03.text = i.toString()
                4 -> binding2.btn04.text = i.toString()
                5 -> binding2.btn05.text = i.toString()
                6 -> binding2.btn06.text = i.toString()
                7 -> binding2.btn07.text = i.toString()
                8 -> binding2.btn08.text = i.toString()
                9 -> binding2.btn09.text = i.toString()
                10 -> binding2.btn00.text = i.toString()
            }
            positions[i] = toPos
        }

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

                    //ENVIAR DATOS
                    val bundle = Bundle()
                    bundle.putSerializable("DATOSUSUARIO", response.body())
                    intent.putExtras(bundle)
                    startActivity(intent)
                    numeroAleatorio()
                }else{
                    println("falla")
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

