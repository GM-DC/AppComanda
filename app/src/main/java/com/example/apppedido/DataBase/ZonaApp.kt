package com.example.apppedido.DataBase

import android.app.Application
import androidx.room.Room


class ZonaApp: Application() {

    val room = Room.databaseBuilder(this, ComandaDB::class.java,"Comanda")
        .build()

}