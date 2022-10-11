package com.example.apppedido

import com.example.apppedido.ValidarConfiguracion.Companion.prefs
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class utils {

    fun pricetostringformat(valuenumeric: Double): String {
        return String.format("%,.2f", valuenumeric)
    }

    fun priceSubTotal(price: Double): Double {
        val igv = prefs.getIGV().toDouble()
        val montoBase = 100*price/(100 + igv)
        return montoBase
    }

    fun priceIGV(price: Double): Double {
        val igv = prefs.getIGV().toDouble()
        val IGV = price*igv/(100 + igv)
        return IGV
    }

    fun getFecha(): String {
        return SimpleDateFormat("dd/MM/yyyy").format(LocalDateTime.now())
    }

    fun fechaActual(): LocalDateTime {
        val fechaActual = LocalDateTime.now()
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val fechaSalida = fechaActual.format(dtf)

        return LocalDateTime.parse(fechaSalida)
    }

    fun formatearFecha(fecha:String):String{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val output = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        val d: Date = sdf.parse(fecha)
        val formattedTime = output.format(d)

        return formattedTime
    }
}