package com.example.apppedido.domain.Model

import java.math.BigDecimal
data class DataClassPedido (

    //var mesa:String
    //var zona:String
    var cantidad:Int,
    val namePlato:String,
    val categoria:String,
    val precio:Double,
    var precioTotal:Double,
    var observacion:String,
    var estadoPedido:String,
    var idProducto:Int,

    )

