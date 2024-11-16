package co.edu.udea.compumovil.gr07_20242.lab2

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaPlayerViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackUrl: String? = null

    fun playTrack(streamUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (currentTrackUrl != streamUrl) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(streamUrl)
                    prepare()
                    start()
                }
                currentTrackUrl = streamUrl
            } else {
                mediaPlayer?.start()
            }
        }
    }

    fun pauseTrack() {
        mediaPlayer?.pause()
    }

    fun stopTrack() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrackUrl = null
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

