package com.example.apppedido.application.View

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.apppedido.R
import com.example.apppedido.ValidarConfiguracion
import com.example.apppedido.ValidarConfiguracion.Companion.prefs
import com.example.apppedido.domain.Model.DCLoginUser
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActyLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acty_login)
        checkValores()
        dataDialogo()
    }

    private fun checkValores() {
        if (prefs.getPuerto().isNotEmpty() || prefs.getDominio().isNotEmpty()){
            goToActyUsuario()
        }
    }

    fun dataDialogo() {
        val btn_ingresar = findViewById<Button>(R.id.btn_ingresar)
        val et_host = findViewById<EditText>(R.id.et_host)
        val et_port = findViewById<EditText>(R.id.et_port)

        val pd = ProgressDialog(this)
        pd.setMessage("Validando usuario....")
        pd.setCancelable(false)
        pd.create()

        btn_ingresar.setOnClickListener {
            if (et_host.text.toString() == "" || et_port.text.toString() == ""){
                AlertMessage("Datos inválidos")
            }else{
                prefs.saveDominio(et_host.text.toString())
                prefs.savePuerto(et_port.text.toString())

                goToActyUsuario()
            }
        }
    }

    fun goToActyUsuario(){
        startActivity(Intent(this, ActyUsuario::class.java))
        finish()
    }

    fun AlertMessage(mensaje: String?) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Información")
        builder.setMessage(mensaje)
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
        val dialogMessage = builder.create()
        dialogMessage.show()
    }

}
