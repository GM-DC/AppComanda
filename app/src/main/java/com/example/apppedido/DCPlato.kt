package com.example.apppedido

import com.google.gson.annotations.SerializedName

data class DCPlatoItem(
    @SerializedName("idPlato") val Codigo: String,
    val IdCategoria: String,
    @SerializedName("Nombre") val namePlato: String,
    val PrecioVenta: Double
)