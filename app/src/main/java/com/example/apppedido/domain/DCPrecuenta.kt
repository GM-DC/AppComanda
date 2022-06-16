package com.example.apppedido.domain.Model

data class DCPrecuenta(
    val detalle: List<ListDetalle>,
    val fechayhora: String,
    val mesa: String,
    val mesero: String,
    val numerO_PEDIDO: String,
    val observaciones: String,
    val zona: String
)

data class ListDetalle(
    val cantidad: Int,
    val importe: Int,
    val nombre: String,
    val precio: Int
)