package com.example.apppedido

data class DataClassPedido (

    var cantidad:Int,
    val namePlato:String,
    val categoria:String,
    val precio:Float,
    var precioTotal:Float,
    var observacion:String

        )