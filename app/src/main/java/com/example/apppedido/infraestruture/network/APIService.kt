package com.example.apppedido.infraestruture.network

import DCOrdenPedido
import com.example.apppedido.domain.DCPedidoMesaItem
import com.example.apppedido.domain.Model.DCPedidoXMesa
import com.example.apppedido.domain.Model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET("api/Users/")
    suspend fun getUsuario(): Response<List<DCUsuarioItem>>

    @GET("api/TablasBasicas/Detail?filter=codigo eq 'CDG_PISO'&select=nombre,numero")
    suspend fun getZonas(): Response<List<DCZonaItem>>

    @GET("api/Producto")
    suspend fun getPlatoBuscado(): Response<List<DCPlatoItem>>

    @GET("api/Mesas")
    suspend fun getMesa(@Query("filter") filter:String) : Response<List<DCMesaItem>>

    @GET("api/TablasBasicas/Detail?filter=codigo eq 'CATE_PROD' and referencia4 eq 'R'&select=nombre,numero&orderby=nombre asc")
    suspend fun getCategoria() : Response<List<DCCategoriaItem>>

    @GET("/{nombrecategoria},{moneda}")
    suspend fun getPlato(@Path("nombrecategoria") nombrecategoria:String, @Path("moneda") moneda:String) : Response<List<DCPlatoItem>>

    @GET("api/Producto?select=nombre,codigo,idcategoria,precioVenta&orderby=nombre asc")
    suspend fun getPlatoNombre(@Query("filter") filter:String) : Response<List<DCPlatoItem>>

    @POST("api/Users/Login")
    suspend fun checkLoginComanda(@Body loginMozo: DCLoginUser) : Response<DCLoginDatosExito>





    @GET("api/Pedido? orderby=idPedido desc & top=1")
    suspend fun getPedidoZonaMesa(@Query("filter") filter:String) : Response<DCPedidoXMesa>

    @GET("/api/Pedido/PedidoMesa/{id}")
    suspend fun getPrePedidos(@Path("id") id:String) : Response<List<DCPedidoMesaItem>>





    @POST("api/Pedido/CreateOrder")
    fun postOrdenPedido(@Body ordenPedido: DCOrdenPedido) : Call<DCOrdenPedido>



    @PUT("/api/Mesas/EstadoMesa/{Piso}/{Mesa}/{Estado}")
    suspend fun putCambiarEstadoMesa(@Path("Piso") idZona:String, @Path("Mesa") idMesa:Int, @Path("Estado") estadoMesa:String) : Response<Void>

}