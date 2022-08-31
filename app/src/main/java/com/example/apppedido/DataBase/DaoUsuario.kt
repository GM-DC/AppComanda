package com.example.apppedido.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoUsuario {

    @Query("SELECT EXISTS(SELECT * FROM EntityUsuario)")
    fun isExistsUsuarios(): Boolean

    @Query("SELECT * FROM EntityUsuario")
    fun getAllUsuarios(): List<EntityUsuario>

    @Insert
    fun insertUsuarios( insertzonas: EntityUsuario)

    @Query("DELETE FROM EntityUsuario")
    fun deleteTableUsuarios()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'EntityUsuario'")
    fun clearPrimaryKeyUsuarios()
}