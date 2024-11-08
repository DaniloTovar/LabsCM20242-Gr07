package co.edu.udea.compumovil.gr07_20242.lab2.api.Endpoint

import retrofit2.http.GET

interface EndpointInterface {
    @GET("/")
    suspend fun getEndpoints(): EndpointsList
}