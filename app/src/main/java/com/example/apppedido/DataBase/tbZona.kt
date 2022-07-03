package com.example.apppedido.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class tbZona (

    @PrimaryKey
    val idZona: String,

    val nombreZonas: String,

    )