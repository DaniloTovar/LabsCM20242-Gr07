package co.edu.udea.compumovil.gr07_20242.lab2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException
import okhttp3.OkHttpClient
import okhttp3.Request
import android.os.Environment
import android.util.Log
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StreamTrackWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context,parameters) {

    val context = context

    override suspend fun doWork(): Result {
        val baseUrl = inputData.getString("BASE_URL") ?: Result.failure()
        val trackId = inputData.getString("TRACK_ID") ?: Result.failure()

        // Getting path to stream MP3 file
        val path = fetchBlobContentAndPlay(context, "$baseUrl/v1/tracks/$trackId/stream")

        return try{
            if (path.isNotEmpty()){
                val output = workDataOf(
                    "PATH" to path
                )
                Result.success(output)
            } else{
                Result.failure()
            }
        } catch (e: HttpException){
            Result.retry()
        } catch (e: Exception){
            Result.failure()
        }
    }
}

suspend fun fetchBlobContentAndPlay(context: Context, blobUrl: String): String {
    val client = OkHttpClient()
    val request = Request.Builder().url(blobUrl).build()

    return suspendCancellableCoroutine { continuation ->
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                continuation.resumeWithException(e) // Resume with exception on failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    continuation.resumeWithException(IOException("Unexpected code $response"))
                }

                val inputStream: InputStream = response.body!!.byteStream()
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "streamed_audio.mp3")
                val outputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                println("pathStreamUrl ${file.absolutePath}")
                continuation.resume(file.absolutePath) // Resume with the file path
            }
        })
    }
}

