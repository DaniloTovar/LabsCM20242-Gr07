package co.edu.udea.compumovil.gr07_20242.lab2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.edu.udea.compumovil.gr07_20242.lab2.api.Track.TrackApi
import com.google.gson.Gson
import retrofit2.HttpException
import kotlin.random.Random

class SearchTrackWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context,parameters) {
    override suspend fun doWork(): Result {
        val baseUrl = inputData.getString("BASE_URL") ?: Result.failure()
        val trackName = inputData.getString("TRACK_NAME") ?: Result.failure()

        return try{
            val apiService = TrackApi().getApiService(baseUrl.toString())
            val tracksList = apiService.searchTrack(trackName.toString())
            if (tracksList.data.isNotEmpty()){
                println("Random track ${tracksList.data[Random.nextInt(tracksList.data.size)]}")
                val jsonTracks = Gson().toJson(tracksList.data)
                val outputData = workDataOf(
                    "TRACKS_JSON" to jsonTracks
                )
                return Result.success(outputData)
            } else{
                return Result.failure()
            }
        } catch (e: HttpException){
            Result.retry()
        }
    }
}