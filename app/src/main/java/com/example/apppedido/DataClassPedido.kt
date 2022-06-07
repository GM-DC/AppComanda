package com.example.apppedido

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.math.BigDecimal

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
data class DataClassPedido (

    var cantidad:Int,
    val namePlato:String,
    val categoria:String,
    val precio:BigDecimal,
    var precioTotal:BigDecimal,
    var observacion:String



    )

