package com.example.apppedido

import android.app.Application
import com.example.apppedido.domain.AplicationContext

class ValidarConfiguracion:Application() {

    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}