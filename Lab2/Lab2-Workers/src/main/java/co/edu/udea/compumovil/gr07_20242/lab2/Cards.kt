package co.edu.udea.compumovil.gr07_20242.lab2

import android.content.Context
import android.content.res.Resources
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.edu.udea.compumovil.gr07_20242.lab2.api.Track.TrackData
import co.edu.udea.compumovil.gr07_20242.lab2.workers.StreamTrackWorker
import coil.compose.rememberAsyncImagePainter

@Composable
fun TrackList(tracks: List<TrackData>, resources: Resources, context: Context) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(tracks) { track ->
            TrackCard(track, resources, context)
        }
    }
}

@Composable
fun TrackCard(track: TrackData, resources: Resources, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onClickTracks(context=context, endpoint, track)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = track.artworks.size480x480),
                contentDescription = "Track Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${resources.getString(R.string.card_title)}: ${track.title}", style = MaterialTheme.typography.labelMedium)
            Text(text = "${resources.getString(R.string.card_genre)}: ${track.genre}", style = MaterialTheme.typography.labelSmall)
            Text(text = "${resources.getString(R.string.card_duration)}: ${track.duration}", style = MaterialTheme.typography.labelSmall)
            Text(text = "${resources.getString(R.string.card_streamable)}: ${track.isStreamable}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

fun onClickTracks(context: Context, endpoint: String, trackData: TrackData){
    val data = Data.Builder()
        .putString("BASE_URL", endpoint)
        .putString("TRACK_ID", trackData.id)
        .build()

    val streamTrackWorkRequest = OneTimeWorkRequestBuilder<StreamTrackWorker>()
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(streamTrackWorkRequest)
}

fun playTrack(streamUrl: String) {
    val mediaPlayer = MediaPlayer().apply {
        setDataSource(streamUrl)
        prepare()
        start()
    }
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}

