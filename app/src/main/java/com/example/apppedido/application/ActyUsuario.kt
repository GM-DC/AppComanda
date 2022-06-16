package com.example.apppedido.application.View
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.adapters.AdapterUsuario
import com.example.apppedido.domain.Model.DCUsuarioItem
import com.example.apppedido.R
import com.example.apppedido.databinding.ActivityInicioBinding
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


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
        initUsuario()
        getData()
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
    private fun getData() {

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getUsuario()
        //        getRetrofit().getUsuario()
            runOnUiThread {
                if(response.isSuccessful){
                    listaUsuario.clear()
                    listaUsuario.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                }else{
                    println("error de internet")
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

}
