package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.example.data.model.Ayah
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurahNumber: Int = 1,
    val currentAyahNumber: Int = 1,
    val totalAyahsInSurah: Int = 7,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val isRepeatAyah: Boolean = false,
    val isRepeatSurah: Boolean = false,
    val reciterId: String = "alafasy",
    val errorMessage: String? = null
)

class QuranAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnPreparedListener { mp ->
                    _playerState.value = _playerState.value.copy(
                        isBuffering = false,
                        isPlaying = true,
                        durationMs = mp.duration,
                        errorMessage = null
                    )
                    applyPlaybackSpeed(_playerState.value.playbackSpeed)
                    mp.start()
                    startProgressTracker()
                }
                setOnCompletionListener {
                    handlePlaybackComplete()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("QuranAudioPlayer", "MediaPlayer error: what=$what, extra=$extra")
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "Audio playback unavailable for this verse."
                    )
                    stopProgressTracker()
                    true
                }
            }
        }
    }

    fun playAyah(surahNumber: Int, ayahNumber: Int, totalAyahs: Int, reciterId: String = _playerState.value.reciterId) {
        initMediaPlayer()

        val ayah = Ayah(surahNumber, ayahNumber, "", "")
        val url = ayah.getAudioUrl(reciterId)

        _playerState.value = _playerState.value.copy(
            currentSurahNumber = surahNumber,
            currentAyahNumber = ayahNumber,
            totalAyahsInSurah = totalAyahs,
            reciterId = reciterId,
            isBuffering = true,
            isPlaying = false,
            errorMessage = null,
            currentPositionMs = 0
        )

        scope.launch(Dispatchers.IO) {
            try {
                mediaPlayer?.apply {
                    reset()
                    setDataSource(url)
                    prepareAsync()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _playerState.value = _playerState.value.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Unable to connect to audio server. Please check internet connection."
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            _playerState.value = _playerState.value.copy(isPlaying = false)
            stopProgressTracker()
        } else {
            mp.start()
            _playerState.value = _playerState.value.copy(isPlaying = true)
            startProgressTracker()
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
    }

    fun nextAyah() {
        val state = _playerState.value
        if (state.currentAyahNumber < state.totalAyahsInSurah) {
            playAyah(state.currentSurahNumber, state.currentAyahNumber + 1, state.totalAyahsInSurah)
        }
    }

    fun prevAyah() {
        val state = _playerState.value
        if (state.currentAyahNumber > 1) {
            playAyah(state.currentSurahNumber, state.currentAyahNumber - 1, state.totalAyahsInSurah)
        }
    }

    fun toggleRepeatAyah() {
        val newRepeat = !_playerState.value.isRepeatAyah
        _playerState.value = _playerState.value.copy(isRepeatAyah = newRepeat)
    }

    fun toggleRepeatSurah() {
        val newRepeat = !_playerState.value.isRepeatSurah
        _playerState.value = _playerState.value.copy(isRepeatSurah = newRepeat)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let { mp ->
                try {
                    val params = mp.playbackParams
                    params.speed = speed
                    mp.playbackParams = params
                } catch (e: Exception) {
                    Log.w("QuranAudioPlayer", "Could not set playback speed: ${e.message}")
                }
            }
        }
    }

    private fun handlePlaybackComplete() {
        val state = _playerState.value
        if (state.isRepeatAyah) {
            playAyah(state.currentSurahNumber, state.currentAyahNumber, state.totalAyahsInSurah)
        } else if (state.currentAyahNumber < state.totalAyahsInSurah) {
            playAyah(state.currentSurahNumber, state.currentAyahNumber + 1, state.totalAyahsInSurah)
        } else if (state.isRepeatSurah) {
            playAyah(state.currentSurahNumber, 1, state.totalAyahsInSurah)
        } else {
            _playerState.value = _playerState.value.copy(isPlaying = false, currentPositionMs = 0)
            stopProgressTracker()
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _playerState.value = _playerState.value.copy(
                            currentPositionMs = mp.currentPosition,
                            durationMs = mp.duration
                        )
                    }
                }
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun stop() {
        stopProgressTracker()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {}
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(isPlaying = false, isBuffering = false)
    }
}
