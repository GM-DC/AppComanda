package com.example.apppedido.domain.Model

import java.math.BigDecimal
data class DataClassPedido (

    var cantidad:Int,
    val namePlato:String,
    val categoria:String,
    val precio:BigDecimal,
    var precioTotal:BigDecimal,
    var observacion:String
    )

