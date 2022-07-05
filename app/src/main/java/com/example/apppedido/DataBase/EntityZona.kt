package com.example.apppedido.DataBase

import androidx.room.ColumnInfo
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityZona (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,

    @ColumnInfo(name = "idZona")
    val idZona: String,

    @ColumnInfo(name = "nombreZonas")
    val nombreZonas: String,

)