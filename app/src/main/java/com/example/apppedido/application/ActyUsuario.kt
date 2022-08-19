package com.example.apppedido.application.View
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.apppedido.R
import com.example.apppedido.ValidarConfiguracion
import com.example.apppedido.databinding.ActivityInicioBinding
import com.example.apppedido.domain.Model.DCUsuarioItem
import com.example.apppedido.infraestruture.adapters.AdapterUsuario
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*


class ActyUsuario : AppCompatActivity() {


    //***************INICIO ATRIBUTOS********************
    private lateinit var binding: ActivityInicioBinding
    private lateinit var adapter: AdapterUsuario
    val listaUsuario = ArrayList<DCUsuarioItem>()
    var apiInterface: APIService? = null
    //***************FIN ATRIBUTOS********************

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiInterface = RetrofitCall.client?.create(APIService::class.java) as APIService

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = null // Sin titulo


        //INICIA LOS DATOS DE USUARIOS
        initUsuario()
        getData2()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.nemu_usuario, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.it_cerrar -> dialogueCerrar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogueCerrar() {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)

        dialog.setTitleText("Cerrar Sesión")
        dialog.setContentText("Se cerrara la sesión y eliminar los datos temporales ¿Desea continuar?")

        dialog.setConfirmText("SI").setConfirmButtonBackgroundColor(Color.parseColor("#013ADF"))
        dialog.setConfirmButtonTextColor(Color.parseColor("#ffffff"))

        dialog.setCancelText("NO").setCancelButtonBackgroundColor(Color.parseColor("#c8c8c8"))
        dialog.setCancelable(false)

        dialog.setCancelClickListener { sDialog -> // Showing simple toast message to user
            sDialog.cancel()
        }
        dialog.setConfirmClickListener { sDialog ->
            borrarPrefs()
            sDialog.cancel()
        }
        dialog.show()
    }

    private fun borrarPrefs() {
        ValidarConfiguracion.prefs.wipe()
        val intent = Intent(this, ActyLogin::class.java)
        startActivity(intent)
        finish()
    }


    //**************   INICIAR DATOS    *********************
    fun initUsuario(){
        val rviUsuario = findViewById<RecyclerView>(R.id.rvUsuarios)
        rviUsuario.layoutManager = GridLayoutManager(this,2,RecyclerView.VERTICAL,false)
        adapter = AdapterUsuario(listaUsuario) {dataClassUsuario -> onItemSelected(dataClassUsuario)}
        rviUsuario.adapter = adapter
    }
    fun onItemSelected(dataClassUsuario: DCUsuarioItem) {
        val intent = Intent(this, activityPasscode::class.java)
        val usuarioMozo: String = dataClassUsuario.nombre.uppercase()
        val idMozo: String = dataClassUsuario.codigo
        intent.putExtra("USUARIOMOZO",usuarioMozo)
        intent.putExtra("IDMOZO",idMozo)
        startActivity(intent)
    }

    fun getData2(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = apiInterface!!.getUsuario2()
            call.enqueue(object : Callback<List<DCUsuarioItem>>{
                override fun onResponse( call: Call<List<DCUsuarioItem>>, response: Response<List<DCUsuarioItem>>) {
                    listaUsuario.clear()
                    listaUsuario.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<List<DCUsuarioItem>>, t: Throwable) {
                    Toast.makeText(this@ActyUsuario, "VERIFICAR SU INTERNET", Toast.LENGTH_LONG).show()
                }

            })
        }
    }



    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getUsuario()
            //getRetrofit().getUsuario()
            runOnUiThread {
                if(response.isSuccessful){
                    listaUsuario.clear()
                    listaUsuario.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                }else{
                    Toast.makeText(this@ActyUsuario, "VERIFIQUE SU CONECCION", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

/*
        val call: Call<List<DCUsuarioItem>> = getRetrofit().getUsuario()
        call.enqueue(object : Callback<List<DCUsuarioItem>> {
            override fun onResponse(call: Call<List<DCUsuarioItem>>?, response: Response<List<DCUsuarioItem>>?) {
                listaUsuario.clear()
                response!!.body()?.let { listaUsuario.addAll(it) }
                adapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<List<DCUsuarioItem>>?, t: Throwable?) {
                println("Error ${t?.message}")
            }
        })
*/

}
