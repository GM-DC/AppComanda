package com.example.apppedido.domain.Model

import com.google.gson.annotations.SerializedName

data class DCPlatoItem(
    @SerializedName("Codigo")
    val idPlato: String,
    val IdCategoria: String,
    @SerializedName("Nombre")
    val namePlato: String,
    val PrecioVenta: Double
)