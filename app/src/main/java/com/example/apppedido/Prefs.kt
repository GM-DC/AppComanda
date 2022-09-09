package com.example.apppedido

import android.content.Context

class Prefs (val contexto:Context){
    val SHARE_DB = "Mydtb"
    val SHARE_DOMINO = "dominio"
    val SHARE_PUERTO = "puerto"
    val SHARE_IGV = "igv"
    val SHARE_CONTRASENA = "contrasena"

    val storege = contexto.getSharedPreferences(SHARE_DB,0)

    fun saveDominio(dominio:String){
        storege.edit().putString(SHARE_DOMINO,dominio).apply()
    }

    fun savePuerto(puerto:String){
        storege.edit().putString(SHARE_PUERTO,puerto).apply()
    }

    fun saveIGV(igv:String){
        storege.edit().putString(SHARE_IGV,igv).apply()
    }

    fun saveContrasena(contrasena:String){
        storege.edit().putString(SHARE_CONTRASENA,contrasena).apply()
    }

    fun getDominio(): String {
        return storege.getString(SHARE_DOMINO,"")!!
    }

    fun getPuerto(): String {
        return storege.getString(SHARE_PUERTO,"")!!
    }

    fun getIGV(): String {
        return storege.getString(SHARE_IGV,"")!!
    }

    fun getContrasena(): String {
        return storege.getString(SHARE_CONTRASENA,"")!!
    }

    fun wipe(){
        storege.edit().clear().apply()
    }


}