package com.example.apppedido.DataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityCategoria (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,

    @ColumnInfo(name = "idCategoria")
    val idCategoria: String,

    @ColumnInfo(name = "nameCategoria")
    val nameCategoria: String,

    )
