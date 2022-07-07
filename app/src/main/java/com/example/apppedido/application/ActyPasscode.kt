package com.example.apppedido.application.View

import com.example.apppedido.domain.Model.DCLoginUser
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.databinding.ActivityPasscodeBinding
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class activityPasscode : AppCompatActivity()  {

    private lateinit var binding2 : ActivityPasscodeBinding
    var apiInterface: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding2.root)
        apiInterface = RetrofitCall.client?.create(APIService::class.java) as APIService

        recibirDatos()
        numeroAleatorio()
    }

    //NUMERO ALEATORIO
    private fun numeroAleatorio(){

       binding2.btn01.text = "1"
       binding2.btn02.text = "2"
       binding2.btn03.text = "3"
       binding2.btn04.text = "4"
       binding2.btn05.text = "5"
       binding2.btn06.text = "6"
       binding2.btn07.text = "7"
       binding2.btn08.text = "8"
       binding2.btn09.text = "9"
       binding2.btn00.text = "0"

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
    //PRESIONAR NUMERO Y VALIDAR
    private fun numeroPresionado(digito:String){
        binding2.txtCodigo.text = "${binding2.txtCodigo.text}${digito}"
        validarDatos ()
    }
    //RECIBIR DATOS DE ACTIVITY USUARIO
    fun recibirDatos(): String {
        val nombre = intent.getStringExtra("USUARIOMOZO")
        binding2.tvBienvenidaNombre.text = "BIENVENIDO $nombre"
        return nombre.toString()
    }
    //VALIDAR LOS DATOS
    fun validarDatos (){
        val idMozo = intent.getStringExtra("IDMOZO")
        val passMozo = binding2.txtCodigo.text.toString()
        if (binding2.txtCodigo.length()==6){
            getDataLogin(idMozo!!,passMozo)
            binding2.txtCodigo.text = ""
        }
    }
    //OBTIENE LA INFORMACION DE LA API MESA
    private fun getDataLogin(usuarioMozo:String,passMozo:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.checkLoginComanda(DCLoginUser("$usuarioMozo","$passMozo"))
            runOnUiThread {
                if(response.isSuccessful){
                    val intent = Intent(applicationContext, ActyPanelPedidos::class.java)
                    //ENVIAR DATOS
                    val bundle = Bundle()
                    val DatosUsuario = response.body()

                    bundle.putSerializable("DATOSUSUARIO", DatosUsuario)

                    intent.putExtras(bundle)
                    startActivity(intent)
                    numeroAleatorio()
                    finish()
                }
            }
        }
    }
}

