package com.example.apppedido.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EntityZona::class], version = 1)
abstract class ComandaDB : RoomDatabase() {
    abstract fun daoZona(): DaoZona
}