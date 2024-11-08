package co.edu.udea.compumovil.gr07_20242.lab2.api.Track

import com.google.gson.annotations.SerializedName

data class TrackData(
    @SerializedName("artwork") val artworks: TrackArtwork,
    @SerializedName("description") val description: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("id") val id: String,
    @SerializedName("track_cid") val cid: String,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("downloadable") val downloadable: Boolean,
    @SerializedName("is_streamable") val isStreamable: Boolean
)
