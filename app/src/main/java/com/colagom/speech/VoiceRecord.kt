package com.colagom.speech

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import com.colagom.common.Channel
import com.colagom.common.Format
import com.colagom.common.WaveHeader

class VoiceRecord(config: Config = Config.Default) : AudioRecord(
    config.audioSource,
    config.sampleRate,
    config.audioChanel,
    config.audioFormat,
    config.bufferSize
) {

    init {
        NoiseSuppressor.create(audioSessionId)
        AcousticEchoCanceler.create(audioSessionId)
    }

    data class ConfigData(
        val audioSource: Int,
        val sampleRate: Int,
        val audioChannel: Int,
        val audioFormat: Int
    )


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

        fun toWaveHeader(fileLength: Int) =
            WaveHeader.Builder()
                .setChannel(if (audioChanel == AudioFormat.CHANNEL_IN_MONO) Channel.Mono else Channel.Stereo)
                .setSampleRate(sampleRate)
                .setFileLength(fileLength)
                .setFormat(Format.PCM)
                .build()

        object Default : Config() {
            override val value: ConfigData
                get() = ConfigData(
                    MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
        }
    }
}