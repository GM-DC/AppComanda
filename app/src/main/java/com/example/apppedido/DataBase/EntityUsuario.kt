package com.example.apppedido.DataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class EntityUsuario (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val codigo: String,

    @ColumnInfo
    val nombre: String
)