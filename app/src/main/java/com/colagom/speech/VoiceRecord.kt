package com.colagom.speech

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class VoiceRecord(config: Config = Config.Default) : AudioRecord(
    config.audioSource,
    config.sampleRate,
    config.audioChanel,
    config.audioFormat,
    config.bufferSize
) {

    init {
//        NoiseSuppressor.create(audioSessionId)
//        AcousticEchoCanceler.create(audioSessionId)
    }

    data class ConfigData(
        val audioSource: Int,
        val sampleRate: Int,
        val audioChannel: Int,
        val audioFormat: Int
    ) {
        val chnnelSize = if (audioChannel == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        val bitPerSample = if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 16 else 8
        // sample rate * TIME_INVERVAL / 1000
        val bytePerFrame get() = sampleRate * 120 / 1000
        val minBufferSize
            get() = bytePerFrame * chnnelSize * bitPerSample / 4
    }

    sealed class Config {
        abstract val value: ConfigData

        val sampleRate get() = value.sampleRate
        val audioSource get() = value.audioSource
        val audioChanel get() = value.audioChannel
        val audioFormat get() = value.audioFormat
        val bufferSize
            get() = getMinBufferSize(
                sampleRate,
                audioChanel,
                audioFormat
            ) * 2

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