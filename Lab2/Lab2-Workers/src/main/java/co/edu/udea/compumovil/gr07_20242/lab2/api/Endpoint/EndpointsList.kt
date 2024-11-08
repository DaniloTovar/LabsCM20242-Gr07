package co.edu.udea.compumovil.gr07_20242.lab2.api.Endpoint

import com.google.gson.annotations.SerializedName

data class EndpointsList(
    @SerializedName("data") val data: List<String>
)
