package com.example.apppedido.domain.Model

import java.math.BigDecimal
data class DataClassPedido (

    var cantidad:Int,
    val namePlato:String,
    val categoria:String,
    val precio:Double,
    var precioTotal:Double,
    var observacion:String,
    var estadoPedido:String
    )

