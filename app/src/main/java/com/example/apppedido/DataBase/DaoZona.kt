package com.example.apppedido.DataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.apppedido.domain.Model.DCZonaItem

@Dao
interface DaoZona {

    @Query("SELECT * FROM tbZona")
    suspend fun getAllZonas(): tbZona

    @Insert
    suspend fun insertZona( insertzonas: tbZona)

    @Query("SELECT * FROM tbZona WHERE idZona = :id")
    fun getByZonas(id: String): List<tbZona>

    @Delete
    fun deleteZona(deletezonas: List<tbZona>)

}