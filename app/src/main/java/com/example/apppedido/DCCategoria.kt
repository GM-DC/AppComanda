package com.example.apppedido

import com.google.gson.annotations.SerializedName

data class DCCategoriaItem(
    @SerializedName("Nombre") val nameCategoria: String,
    @SerializedName("Numero") val idCategoria: String
)