package com.example.apppedido.domain

import com.google.gson.annotations.SerializedName
import lombok.Builder
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Builder
class DCComanda : ArrayList<DCComandaItem>()

data class DCComandaItem(
    val numerO_PEDIDO: String,
    val destino: String,
    val zona: String,
    val mesa: String,
    val mesero: String,
    val rutacomanda: String,
    val fechayhora: String,
    val detalle: List<Detalle>
)

data class Detalle(
    val iD_PRODUCTO: Int,
    val producto: String,
    val cantidad: Int,
    val precio: Double,
    val importe: Double,
    val observacion: String,
    val noM_IMP: String?,
    val secuencia: Int
)

