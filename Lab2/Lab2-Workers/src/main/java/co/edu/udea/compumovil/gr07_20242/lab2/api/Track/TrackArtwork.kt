package co.edu.udea.compumovil.gr07_20242.lab2.api.Track

import com.google.gson.annotations.SerializedName

data class TrackArtwork(
    @SerializedName("150x150") val size150x150 : String,
    @SerializedName("480x480") val size480x480 : String,
    @SerializedName("1000x1000") val size1000x1000 : String,
)
