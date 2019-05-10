package com.colagom.speech

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor

class VoiceRecord(config: Config = Config.Default) : AudioRecord(
    config.value.audioSource,
    config.value.sampleRate,
    config.value.channel,
    config.value.audioFormat,
    config.value.minBufferSize
) {
    val minBufferSize: Int

    init {
        minBufferSize = config.value.minBufferSize
        NoiseSuppressor.create(audioSessionId)
        AcousticEchoCanceler.create(audioSessionId)
    }


    data class ConfigData(
        val audioSource: Int,
        val sampleRate: Int,
        val channel: Int,
        val audioFormat: Int
    ) {
        val minBufferSize
            get() = getMinBufferSize(sampleRate, channel, audioFormat)
    }

    sealed class Config {
        abstract val value: ConfigData

        object Default : Config() {
            override val value: ConfigData
                get() = ConfigData(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
        }
    }
}