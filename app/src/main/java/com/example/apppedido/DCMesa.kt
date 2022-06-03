package com.example.apppedido

import com.google.gson.annotations.SerializedName


data class DCMesaItem(
    val estado: String,
    val estadoTrans: String,
    val idMesa: Int,
    val piso: String,
    val secuencia: Int,
    val tipo: String
)
