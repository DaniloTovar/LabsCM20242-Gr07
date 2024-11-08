package co.edu.udea.compumovil.gr07_20242.lab2

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.edu.udea.compumovil.gr07_20242.lab2.api.Track.TrackData
import co.edu.udea.compumovil.gr07_20242.lab2.ui.theme.Labs20242Gr07Theme
import co.edu.udea.compumovil.gr07_20242.lab2.workers.EndpointWorker

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val resources = resources

        executeEndpointWorker(this)

        setContent {
            Labs20242Gr07Theme {
                Surface (modifier = Modifier.fillMaxSize()){
                    CasterApp(resources, this)
                }
            }
        }
    }

    fun executeEndpointWorker(context: Context){
        val workRequest =  OneTimeWorkRequestBuilder<EndpointWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, { workInfo ->
                if (workInfo!=null && workInfo.state.isFinished){
                    endpoint = workInfo.outputData.getString("SELECTED_ENDPOINT")?: ""
                }
            })
    }
}