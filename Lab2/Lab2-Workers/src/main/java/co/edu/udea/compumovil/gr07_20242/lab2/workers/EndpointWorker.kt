package co.edu.udea.compumovil.gr07_20242.lab2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.edu.udea.compumovil.gr07_20242.lab2.api.Endpoint.EndpointApi
import retrofit2.HttpException
import kotlin.random.Random

class EndpointWorker(context: Context, params:WorkerParameters) : CoroutineWorker(context,params){
    override suspend fun doWork(): Result {
        return try {
            val endpointApi = EndpointApi().api.getEndpoints()
            if (endpointApi.data.isNotEmpty()){
                val randomEndpoint = endpointApi.data[Random.nextInt(endpointApi.data.size)]
                val outputData = workDataOf(
                    "SELECTED_ENDPOINT" to randomEndpoint
                )
                println("Selected endopint is $randomEndpoint")
                Result.success(outputData)
            } else{
                Result.failure()
            }
        } catch (e: HttpException){
            Result.retry()
        }
    }
}