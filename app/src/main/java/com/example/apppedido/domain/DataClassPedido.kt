package com.example.apppedido.domain.Model

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString

import java.math.BigDecimal
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
data class DataClassPedido (

    var cantidad:Int,
    val namePlato:String,
    val categoria:String,
    val precio:BigDecimal,
    var precioTotal:BigDecimal,
    var observacion:String
    )

