package com.example.apppedido

import android.content.Context

class Prefs (val contexto:Context){
    val SHARE_DB = "Mydtb"
    val SHARE_DOMINO = "dominio"
    val SHARE_PUERTO = "puerto"
    val SHARE_IGV = "igv"
    val SHARE_IPPRECUENTA= "ipprecuenta"

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

    fun saveIPPrecuenta(ipprecuenta:String){
        storege.edit().putString(SHARE_IPPRECUENTA,ipprecuenta).apply()
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

    fun getIPPrecuenta(): String {
        return storege.getString(SHARE_IPPRECUENTA,"")!!
    }

    fun wipe(){
        storege.edit().clear().apply()
    }


}