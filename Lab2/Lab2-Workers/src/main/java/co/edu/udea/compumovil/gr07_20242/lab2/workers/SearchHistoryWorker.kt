package co.edu.udea.compumovil.gr07_20242.lab2.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class SearchHistoryWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
        val searchedName = inputData.getString("SEARCHED_NAME") ?: Result.failure()
        override fun doWork(): Result {
            println("Searched: $searchedName") // Save on non-existent database
            val outputData = workDataOf(
                "SEARCHED_NAME" to searchedName
            )
            return Result.success(outputData)
    }
}