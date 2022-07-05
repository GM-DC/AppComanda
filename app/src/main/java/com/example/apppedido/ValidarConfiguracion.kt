package com.example.apppedido

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.apppedido.DataBase.ComandaDB
import com.example.apppedido.domain.AplicationContext

class ValidarConfiguracion:Application() {

    companion object{
        lateinit var prefs: Prefs
        lateinit var database: ComandaDB

    }

    override fun onCreate() {
        super.onCreate()

        prefs = Prefs(applicationContext)
        database =  Room.databaseBuilder(this, ComandaDB::class.java, "ComandaDB").build()
    }

}