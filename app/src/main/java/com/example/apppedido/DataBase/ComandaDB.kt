package com.example.apppedido.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EntityZona::class, EntityCategoria::class],
    version = 2)
abstract class ComandaDB : RoomDatabase() {
    abstract fun daoZona(): DaoZona
    abstract fun daoCategoria(): DaoCategoria
}