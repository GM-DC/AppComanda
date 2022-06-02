package com.example.apppedido

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Url

interface APIService {
    @GET("api/Users/")
    fun getUsuario(): Call<List<DCUsuarioItem>>

    @GET("api/TablasBasicas/Detail?filter=codigo eq 'CDG_PISO'")
    fun getZonas(): Call<List<DCZonaItem>>
}