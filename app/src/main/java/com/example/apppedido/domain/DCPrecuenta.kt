package com.example.apppedido.domain.Model

import lombok.Builder
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Builder
data class DCPrecuenta(
    var numerO_PEDIDO: String,
    var mesero: String,
    val zona: String,
    val mesa: String,
    val fechayhora: String,
    val observaciones: String,
    val subtotal: String,
    val igv:String,
    val total: String,
    val detalle: List<ListDetalle>
)

data class ListDetalle(
    val cantidad: Int,
    val nombre: String,
    val precio: Double,
    val importe: Double,
)