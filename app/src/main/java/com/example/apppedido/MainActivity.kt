package com.example.apppedido
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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


        




        /////BOTON SIGUIENTE ////
        binding.btnSigiente.setOnClickListener {
            val intent = Intent(this, activityPasscode::class.java)
            startActivity(intent)
        }
        ///


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

    val listaZona = listOf<DataClassZona>(
        DataClassZona("1","Zona 01"),
        DataClassZona("2","Zona 02"),
        DataClassZona("3","Zona 03"),
        DataClassZona("4","Zona 04"),
        DataClassZona("5","Zona 05"),
        DataClassZona("6","Zona 06"),
        DataClassZona("7","Zona 07"),
        DataClassZona("8","Zona 08"),
        DataClassZona("9","Zona 09"),
        DataClassZona("10","Zona 10"),
        DataClassZona("11","Zona 11"),
        DataClassZona("10","Zona 12"),
        DataClassZona("11","Zona 13")
    )

    fun initUsuario(){
        val rviUsuario = findViewById<RecyclerView>(R.id.rvUsuarios)

        rviUsuario.layoutManager = LinearLayoutManager(this)
        val adapter = AdapterUsuario(listaUsuario)
        rviUsuario.adapter = adapter
    }


}
