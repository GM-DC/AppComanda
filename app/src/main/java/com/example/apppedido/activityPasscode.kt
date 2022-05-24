package com.example.apppedido

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.apppedido.databinding.ActivityPasscodeBinding
import kotlin.random.Random
import java.lang.*
import java.util.*


class activityPasscode : AppCompatActivity() {

    private lateinit var binding2 : ActivityPasscodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding2.root)


        var numero = ((Math.random() * 10)).toInt()
        val grupo = Array(10){-1}
        var posicion:Int = 0
        var recorrido:Int = 0

        println("Numero inicial: $numero")

        for (i in 0..9){
            println("------------caso $i----------------")

            do {

                numero = ((Math.random() * 10)).toInt()

            }while(numero==grupo[posicion])

            grupo.set(posicion, numero)
            posicion+=1

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


        /*
        for(i in 0..9){
            println("------------caso $i----------------")
            println(grupo[posicion]==numero)
            println("grupo[$posicion] ${grupo[i]}==$numero")
            numero = (Math.random() * 10).toInt()

                if (grupo[i] == numero) {
                    numero = (Math.random() * 10).toInt()
                    println("Nuevo numero $numero")
                    i = 0

                } else {
                    grupo.set(posicion, numero)
                    println("numero ingresado: Pos - $posicion // Num - $numero ")
                    posicion += 1

            }
        }
        */


        println(grupo[2])
        println(numero)
        println(posicion)

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



        /////BOTON SIGUIENTE ////
        binding2.btnSiguiente2.setOnClickListener {
            val intent = Intent(this, panelPedido::class.java)
            startActivity(intent)
        }
        ///

    }



    private fun numeroPresionado(digito:String){
        binding2.txtCodigo.text = "${binding2.txtCodigo.text}${digito}"
    }

    private fun Random.nextInt(range: IntRange): Int {
        return range.start + nextInt(range.last - range.start)
    }

}