package com.example.apppedido.DataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoZona {

    @Query("SELECT * FROM EntityZona")
    fun getAllZonas(): List<EntityZona>

    @Query("SELECT idZona FROM EntityZona WHERE id = :id")
    fun getZonaId(id: Int): String

    @Query("SELECT nombreZonas FROM EntityZona WHERE id = :id")
    fun getZonaNombre(id: Int): String

    @Insert
    fun insertZona( insertzonas: EntityZona)

    @Query("DELETE FROM EntityZona")
    fun deleteTable()

    @Query("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'EntityZona'")
    fun clearPrimaryKey()

}