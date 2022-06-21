package com.example.apppedido.domain.Model

import lombok.Builder
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Builder
data class DCPrecuenta(
    var numerO_PEDIDO: String,
    val mesero: String,
    val zona: String,
    val mesa: String,
    val fechayhora: String,
    val observaciones: String,
    val precioTotal: Double,
    val igv:Double,
    val detalle: List<ListDetalle>
)

data class ListDetalle(
    val cantidad: Int,
    val nombre: String,
    val precio: Double,
    val importe: Double,
)