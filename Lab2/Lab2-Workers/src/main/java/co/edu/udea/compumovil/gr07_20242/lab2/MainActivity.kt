package co.edu.udea.compumovil.gr07_20242.lab2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.edu.udea.compumovil.gr07_20242.lab2.ui.theme.Labs20242Gr07Theme
import co.edu.udea.compumovil.gr07_20242.lab2.workers.EndpointWorker

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val resources = resources

        enableImmersiveMode()

        setContent {
            Labs20242Gr07Theme {
                LaunchedEffect(Unit) {
                    executeEndpointWorker(
                        context = this@MainActivity
                    )
                }

                Surface (modifier = Modifier.fillMaxSize()){
                    CasterApp(resources, this)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            enableImmersiveMode()
        }
    }

    private fun enableImmersiveMode(){
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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