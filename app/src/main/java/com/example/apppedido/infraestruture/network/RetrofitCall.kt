package com.example.apppedido.infraestruture.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitCall {
    private var retrofit: Retrofit? = null
    @JvmStatic
    val client: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES) // connect timeout **PRUEBA
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout **PRUEBA
                .readTimeout(5, TimeUnit.MINUTES) // read timeout **PRUEBA
                .addInterceptor(interceptor)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl("http://heyeldevs-001-site1.gtempurl.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit
        }
}