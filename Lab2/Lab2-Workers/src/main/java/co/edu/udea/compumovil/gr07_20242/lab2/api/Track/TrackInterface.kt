package co.edu.udea.compumovil.gr07_20242.lab2.api.Track

import retrofit2.http.GET
import retrofit2.http.Query

interface TrackInterface {
    @GET("/v1/tracks/search")
    suspend fun searchTrack(
        @Query("query") trackName: String
    ) : TrackList
}