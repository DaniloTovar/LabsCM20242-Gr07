package co.edu.udea.compumovil.gr07_20242.lab2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException
import okhttp3.OkHttpClient
import okhttp3.Request
import android.os.Environment
import co.edu.udea.compumovil.gr07_20242.lab2.playTrack
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class StreamTrackWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context,parameters) {

    val context = context

    override suspend fun doWork(): Result {
        val baseUrl = inputData.getString("BASE_URL") ?: Result.failure()
        val trackId = inputData.getString("TRACK_ID") ?: Result.failure()

        return try{
            fetchBlobContentAndPlay(context, "$baseUrl/v1/tracks/$trackId/stream")
            Result.success()
        } catch (e: HttpException){
            Result.retry()
        } catch (e: Exception){
            Result.failure()
        }
    }
}

fun fetchBlobContentAndPlay(context: Context, blobUrl: String) {
    val client = OkHttpClient()
    val request = Request.Builder().url(blobUrl).build()

    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            if (!response.isSuccessful) {
                throw java.io.IOException("Unexpected code $response")
            }

            val inputStream: InputStream = response.body!!.byteStream()
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "streamed_audio.mp3")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            playTrack(file.absolutePath)
        }
    })
}
