package com.example.apppedido.domain.Model

import com.google.gson.annotations.SerializedName

data class DCZonaItem(
    @SerializedName("Nombre") val nombreZonas: String,
    @SerializedName("Numero") val idZona: String,
)
