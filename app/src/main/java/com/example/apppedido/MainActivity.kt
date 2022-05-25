package com.example.apppedido
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.databinding.ActivityInicioBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUsuario()
        botonSiguiente()

        val layoutmanager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL,false)

    }

    fun botonSiguiente(){
        /////BOTON SIGUIENTE ////
        binding.btnSiguiente2.setOnClickListener {
            val intent = Intent(this, activityPasscode::class.java)
            startActivity(intent)
        }
        ///
    }

    fun initUsuario(){
        val rviUsuario = findViewById<RecyclerView>(R.id.rvUsuarios)

        rviUsuario.layoutManager = GridLayoutManager(this,2,RecyclerView.VERTICAL,false)
        val adapter = AdapterUsuario(listaUsuario)
        rviUsuario.adapter = adapter
    }

    val listaUsuario = listOf<DataClassUsuario>(
        DataClassUsuario("1","Gian"),
        DataClassUsuario("2","Alex"),
        DataClassUsuario("3","Pedro"),
        DataClassUsuario("4","Jose"),
        DataClassUsuario("5","Manuel"),
        DataClassUsuario("6","Marco"),
        DataClassUsuario("7","Ingrid"),
        DataClassUsuario("8","Maria"),
        DataClassUsuario("9","Dessire"),
        DataClassUsuario("10","Dani"),
        DataClassUsuario("11","Ruben"),
        DataClassUsuario("10","Pepe"),
        DataClassUsuario("11","Rubi")
    )

}
