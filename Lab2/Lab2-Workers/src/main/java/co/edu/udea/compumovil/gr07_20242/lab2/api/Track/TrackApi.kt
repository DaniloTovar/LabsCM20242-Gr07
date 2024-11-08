package co.edu.udea.compumovil.gr07_20242.lab2.api.Track

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackApi {
    private var retrofit: Retrofit? = null

    private val gson = GsonBuilder().setLenient().create()

    fun getApiService(baseUrl: String): TrackInterface{
        if(retrofit == null || retrofit?.baseUrl()?.toString() != baseUrl){
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!.create(TrackInterface::class.java)
    }
}