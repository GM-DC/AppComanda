package com.example.apppedido

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET("api/Users/")
    suspend fun getUsuario(): Response<List<DCUsuarioItem>>

    @GET("api/TablasBasicas/Detail?filter=codigo eq 'CDG_PISO'")
    suspend fun getZonas(): Response<List<DCZonaItem>>

    @GET("api/Mesas")
    suspend fun getMesa(@Query("filter") filter:String) : Response<List<DCMesaItem>>

    @GET("http://heyeldevs-001-site1.gtempurl.com/api/TablasBasicas/Detail?filter=codigo eq 'CATE_PROD' and referencia4 eq 'R'&select=nombre,numero&orderby=nombre asc")
    suspend fun getCategoria() : Response<List<DCCategoriaItem>>
}