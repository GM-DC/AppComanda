package com.example.apppedido.domain.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DCLoginUser(
    val usuariomozo: String,
    var passmozo: String,
)

data class DCRespuestaLogin(
    val message: String
)

data class DCLoginDatosExito (
    val cdgmoneda: String,   //
    val cdgpago: String,     //
    val codigO_EMPRESA: String,  //**
    val correlativo: Any,  //**
    val descuento: String,  //
    val estadopedido: String,
    val facturA_ADELANTADA: String,  //
    val iD_CLIENTE: Int,  //---  preguntar
    val iD_COTIZACION: String, //
    val jwtToken: String, //
    val motivo: Any,    //
    @SerializedName("nombreusuario") val nameMozo: String,
    val poR_IGV: String,  //
    val puntO_VENTA: String, //
    val redondeo: String,  //
    val refreshToken: String,
    val seriepedido: String,  //-- preguntar (seriepedido = serie)
    val sucursal: String,  //
    val tipocambio: String,
    val usuario: String,
    val usuarioautoriza: String, //**
    val usuariocreacion: String, //**
    val validez: String //**
) : Serializable