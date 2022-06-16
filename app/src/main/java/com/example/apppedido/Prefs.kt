package com.example.apppedido

import android.content.Context

class Prefs (val contexto:Context){
    val SHARE_DB = "Mydtb"
    val SHARE_DOMINO = "dominio"
    val SHARE_PUERTO = "Pruerto"

    val storege = contexto.getSharedPreferences(SHARE_DB,0)

    fun saveDominio(dominio:String){
        storege.edit().putString(SHARE_DOMINO,dominio).apply()
    }

    fun savePuerto(puerto:String){
        storege.edit().putString(SHARE_DOMINO,puerto).apply()
    }

    fun getDominio(): String {
        return storege.getString(SHARE_DOMINO,"")!!
    }

    fun getPuerto(): String {
        return storege.getString(SHARE_PUERTO,"")!!
    }


}