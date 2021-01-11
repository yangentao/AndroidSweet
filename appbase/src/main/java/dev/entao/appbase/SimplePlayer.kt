package dev.entao.appbase

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener

import java.io.IOException

class SimplePlayer {
    private var player: MediaPlayer? = null

    val isPlaying: Boolean
        get() = player?.isPlaying ?: false

    fun setOnCompletionListener(listener: OnCompletionListener) {
        player?.setOnCompletionListener(listener)
    }

    fun start(path: String) {
        if (isPlaying) {
            stop()
        }

        player = MediaPlayer()
        try {
            player?.setDataSource(path)
            player?.prepare()
            player?.start()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun stop() {
        if (isPlaying) {
            player?.stop()
            player?.reset()
        }
        player?.release()
        player = null
    }
}
