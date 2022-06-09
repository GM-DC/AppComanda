package com.example.apppedido

import com.google.gson.annotations.SerializedName


data class DCMesaItem(
    @SerializedName("idMesa") val idMesa: Int,
    val estado: String,
    val estadoTrans: String,
    @SerializedName("piso") val idZona: String,
    val secuencia: Int,
    val tipo: String
)
