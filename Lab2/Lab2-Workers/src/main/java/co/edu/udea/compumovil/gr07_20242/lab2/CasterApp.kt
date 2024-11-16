package co.edu.udea.compumovil.gr07_20242.lab2

import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.edu.udea.compumovil.gr07_20242.lab2.api.Track.TrackData
import co.edu.udea.compumovil.gr07_20242.lab2.workers.SearchHistoryWorker
import co.edu.udea.compumovil.gr07_20242.lab2.workers.SearchTrackWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


var endpoint: String = ""
var searchHistory: SearchHistory = SearchHistory(mutableListOf(""))
var counter: Int = 0
var tracks: List<TrackData> = mutableListOf()

@Composable
fun CasterApp(resources: Resources, context: Context) {
    val mediaPlayerViewModel: MediaPlayerViewModel = viewModel()

    var searchValue by rememberSaveable { mutableStateOf("") }
    var trackList by rememberSaveable { mutableStateOf(listOf<TrackData>()) }
    var currentTrack by remember { mutableStateOf<TrackData?>(null) }
    var streamUrlNow by rememberSaveable { mutableStateOf("") }
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    if (endpoint.isNotEmpty() && counter == 0) {
        Toast.makeText(context, endpoint, Toast.LENGTH_SHORT).show()
        counter++
    }

    Box(modifier = Modifier.fillMaxSize().padding(top = 36.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                TextField(
                    value = searchValue,
                    placeholder = { Text(text = resources.getString(R.string.search_placeholder)) },
                    onValueChange = { searchValue = it },
                    modifier = Modifier.padding(8.dp).weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearchHistory(context, searchValue)
                            onSearchTracks(context, endpoint, searchValue, onSuccess = { trackList = tracks })
                        }
                    )
                )
                IconButton(
                    onClick = {
                        onSearchHistory(context, searchValue)
                        onSearchTracks(context, endpoint, searchValue, onSuccess = { trackList = tracks })
                    },
                    modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        modifier = Modifier.padding(8.dp),
                        contentDescription = resources.getString(R.string.search_icon)
                    )
                }
            }

            TrackList(tracks = trackList, modifier = Modifier.weight(10f)) { track, streamUrl ->
                currentTrack = track
                isPlaying = true
                streamUrlNow = streamUrl
                mediaPlayerViewModel.playTrack(streamUrl)
            }

            Spacer(modifier = Modifier.weight(0.1f).height(4.dp)) // Push the PlaybackBar to the bottom
            if (streamUrlNow.isNotEmpty()) {
                PlaybackBar(
                    trackTitle = currentTrack?.title ?: "",
                    isPlaying = isPlaying,
                    onPlayPause = {
                        if (isPlaying) {
                            mediaPlayerViewModel.pauseTrack()
                        } else {
                            mediaPlayerViewModel.playTrack(streamUrlNow)
                        }
                        isPlaying = !isPlaying
                    },
                    onStop = {
                        mediaPlayerViewModel.stopTrack()
                        isPlaying = false
                        currentTrack = null
                        streamUrlNow = ""
                    }
                )
            }
        }
    }
}


fun onSearchHistory(context: Context, searchedValue: String){
    val data = Data.Builder()
        .putString("SEARCHED_NAME", searchedValue)
        .build()

    val searchHistoryWorkRequest = OneTimeWorkRequestBuilder<SearchHistoryWorker>()
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(searchHistoryWorkRequest)
    WorkManager.getInstance(context).getWorkInfoByIdLiveData(searchHistoryWorkRequest.id)
        .observeForever({ workInfo ->
            if (workInfo!=null && workInfo.state.isFinished){
                searchHistory.searchHistory.add(workInfo.outputData.getString("SEARCHED_NAME")?: "")
            }
        })
}

fun onSearchTracks(context: Context, endpoint: String, trackName: String, onSuccess: (List<TrackData>) -> Unit){
    val data = Data.Builder()
        .putString("BASE_URL", endpoint)
        .putString("TRACK_NAME", trackName)
        .build()

    val searchTracksWorkRequest = OneTimeWorkRequestBuilder<SearchTrackWorker>()
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(searchTracksWorkRequest)
    WorkManager.getInstance(context).getWorkInfoByIdLiveData(searchTracksWorkRequest.id)
        .observeForever({ workInfo ->
            if (workInfo!=null && workInfo.state.isFinished){
                val jsonTracks = workInfo.outputData.getString("TRACKS_JSON") ?: ""
                if (jsonTracks != ""){
                    val type = object : TypeToken<List<TrackData>>() {}.type
                    tracks = Gson().fromJson(jsonTracks, type)
                    onSuccess(tracks)
                }
            }
        })
}

