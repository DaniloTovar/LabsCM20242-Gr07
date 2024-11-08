package co.edu.udea.compumovil.gr07_20242.lab2.api.Endpoint

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EndpointApi {
    private val BASE_URL = "https://api.audius.co"

    val api: EndpointInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EndpointInterface::class.java)
    }
}