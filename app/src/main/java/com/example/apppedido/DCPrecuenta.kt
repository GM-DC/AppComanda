package com.example.apppedido

data class DCPrecuenta(
    val detalle: List<Detalle>,
    val fechayhora: String,
    val mesa: String,
    val mesero: String,
    val numerO_PEDIDO: String,
    val observaciones: String,
    val zona: String
)

data class Detalle(
    val cantidad: Int,
    val importe: Int,
    val nombre: String,
    val precio: Int
)