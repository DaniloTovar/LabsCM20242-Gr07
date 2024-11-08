package co.edu.udea.compumovil.gr07_20242.lab2.api.Track

import com.google.gson.annotations.SerializedName

data class TrackList(
    @SerializedName("data") val data : List<TrackData>
)
