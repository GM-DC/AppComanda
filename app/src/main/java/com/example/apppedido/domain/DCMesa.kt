package com.example.apppedido.domain.Model

import com.google.gson.annotations.SerializedName


data class DCMesaItem(
    @SerializedName("idMesa") val idMesa: Int,
    val estado: String,
    val estadoTrans: String,
    @SerializedName("piso") val idZona: String,
    val secuencia: Int,
    val tipo: String,
    val idPedido: String,
    val NombreMozo: String
)


data class DCMesaMozo(
    val NombreMozo: String
)