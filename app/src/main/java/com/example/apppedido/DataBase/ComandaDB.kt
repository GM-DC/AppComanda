package com.example.apppedido.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [tbZona::class],version = 1)
abstract class ComandaDB : RoomDatabase() {

    abstract fun zonaDao(): DaoZona

    companion object {
        @Volatile
        private var INSTANCE: ComandaDB? = null

        fun getDatabase(context: Context): ComandaDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ComandaDB::class.java,
                    "Comanda"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}