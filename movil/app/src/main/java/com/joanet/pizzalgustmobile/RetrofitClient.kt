package com.joanet.pizzalgustmobile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Objeto Singleton que proporciona instancias de servicios Retrofit para realizar llamadas a la API.
 */

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5002/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Método para crear una instancia del servicio API principal.
     *
     * @return Una instancia del servicio principal de la API.
     */
    fun createApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    /**
     * Método para crear una instancia del servicio API para cerrar sesión.
     *
     * @return Una instancia del servicio de cierre de sesión de la API.
     */
    fun createLogoutService(): ApiService.ApiLogout {
        return retrofit.create(ApiService.ApiLogout::class.java)
    }

}