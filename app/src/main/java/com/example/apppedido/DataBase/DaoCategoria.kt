package com.example.apppedido.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoCategoria {

    @Query("SELECT * FROM EntityCategoria")
    fun getAllCategoria(): List<EntityCategoria>

    @Query("SELECT idCategoria FROM EntityCategoria WHERE id = :id")
    fun getCategoriaId(id: Int): String

    @Query("SELECT nameCategoria FROM EntityCategoria WHERE id = :id")
    fun getCategoriaNombre(id: Int): String

    @Insert
    fun insertCategoria( insertzonas: EntityCategoria)

    @Query("DELETE FROM EntityCategoria")
    fun deleteTable()

    @Query("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'EntityCategoria'")
    fun clearPrimaryKey()
}