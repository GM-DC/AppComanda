package com.example.apppedido.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoCategoria {

    @Query("SELECT EXISTS(SELECT * FROM EntityCategoria)")
    fun isExistsCategoria(): Boolean

    @Query("SELECT * FROM EntityCategoria")
    fun getAllCategoria(): List<EntityCategoria>

    @Insert
    fun insertCategoria( insertzonas: EntityCategoria)

    @Query("DELETE FROM EntityCategoria")
    fun deleteTableCategoria()

    @Query("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'EntityCategoria'")
    fun clearPrimaryKeyCategoria()
}