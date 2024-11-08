package co.edu.udea.compumovil.gr07_20242.lab2.api.Track

import com.google.gson.annotations.SerializedName

data class TrackStream(
    @SerializedName("data") val streamUrl: String
)
