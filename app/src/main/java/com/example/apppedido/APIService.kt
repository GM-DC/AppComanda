package com.example.apppedido

import DCLoginDatosExito
import DCLoginUser
import DCRespuestaLogin
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET("api/Users/")
    suspend fun getUsuario(): Response<List<DCUsuarioItem>>

    @GET("api/TablasBasicas/Detail?filter=codigo eq 'CDG_PISO'")
    suspend fun getZonas(): Response<List<DCZonaItem>>

    @GET("api/Mesas")
    suspend fun getMesa(@Query("filter") filter:String) : Response<List<DCMesaItem>>

    @GET("api/TablasBasicas/Detail?filter=codigo eq 'CATE_PROD' and referencia4 eq 'R'&select=nombre,numero&orderby=nombre asc")
    suspend fun getCategoria() : Response<List<DCCategoriaItem>>

    @GET("api/Producto?select=nombre,codigo,idcategoria,precioVenta&orderby=nombre asc")
    suspend fun getPlato(@Query("filter") filter:String) : Response<List<DCPlatoItem>>

    @POST("api/Users/Login")
    suspend fun checkLoginComanda(@Body loginMozo: DCLoginUser) : Response<DCLoginDatosExito>
}