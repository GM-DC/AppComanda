package com.example.apppedido.domain

import com.example.apppedido.domain.Model.DataClassPedido
import java.io.Serializable

data class DataClassPedidoBorrador(
    val zona:Int,
    val mesa:Int,
    val pedidoBorrador: List<DataClassPedido>
): Serializable
