package com.iq_academy.malika_ai.helper

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.abs

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */

//Lestining voice

class SoundListener(private val callback: (Boolean) -> Unit) {

    private val SAMPLE_RATE = 44100
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    @SuppressLint("MissingPermission")
    private val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        BUFFER_SIZE
    )

    private var lastSoundTime = 0L

    private val soundCheckRunnable = object : Runnable {
        override fun run() {
            val buffer = ShortArray(BUFFER_SIZE)
            val readResult = audioRecord.read(buffer, 0, BUFFER_SIZE)
            if (readResult > 0) {
                val amplitude = buffer.map { abs(it.toInt()) }.average()
                if (amplitude > 2000) {
                    lastSoundTime = System.currentTimeMillis()
                    callback(true)
                } else if (System.currentTimeMillis() - lastSoundTime > 2000) {
                    lastSoundTime = 0
                    callback(false)
                }
            }
            handler.post(this)
        }
    }

    private val handler = android.os.Handler()

    fun startDetecting() {
        lastSoundTime = System.currentTimeMillis()
        audioRecord.startRecording()
        handler.post(soundCheckRunnable)
    }

    fun stopDetecting() {
        handler.removeCallbacks(soundCheckRunnable)
        audioRecord.stop()
        audioRecord.release()
    }

}