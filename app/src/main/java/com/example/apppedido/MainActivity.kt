package com.example.apppedido
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.databinding.ActivityInicioBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity(swipeDirs: Int) : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private lateinit var adapter: AdapterUsuario
    val listaUsuario = ArrayList<DCUsuarioItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUsuario()
        getData()
    }

    //***********************************
    fun initUsuario(){
        val rviUsuario = findViewById<RecyclerView>(R.id.rvUsuarios)
        rviUsuario.layoutManager = GridLayoutManager(this,2,RecyclerView.VERTICAL,false)
        adapter = AdapterUsuario(listaUsuario) {dataClassUsuario -> onItemSelected(dataClassUsuario)}
        rviUsuario.adapter = adapter
    }

    // FUNCION SELECCIONAR USUARIO
    fun onItemSelected(dataClassUsuario: DCUsuarioItem) {
        val intent = Intent(this, activityPasscode::class.java)
        val nombre: String = dataClassUsuario.nombre.uppercase()
        intent.putExtra("USUARIO",nombre)
        startActivity(intent)
    }


    // OBTERNER DATA
    private fun getData() {

        CoroutineScope(Dispatchers.IO).launch {
            val response = getRetrofit().getUsuario()
            runOnUiThread {
                if(response.isSuccessful){
                    listaUsuario.clear()
                    listaUsuario.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                }else{
                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
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






    // RETROFIT
    fun getRetrofit(): APIService {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://heyeldevs-001-site1.gtempurl.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return  retrofit.create(APIService::class.java)
    }

}
