package com.colagom.common

import java.io.FileOutputStream

fun FileOutputStream.writeWaveHeader(header: WaveHeader) = with(header) {
    writeString("RIFF")
    writeInt(36 + fileLength)
    writeString("WAVE")

    writeString("fmt ")
    writeInt(16)
    writeShort(format.id)
    writeShort(channel.count)
    writeInt(sampleRate)
    writeInt(channel.count * sampleRate * format.bitPerSample / 8)
    writeShort((channel.count * format.bitPerSample / 8).toShort())
    writeShort(format.bitPerSample)

    writeString("data")
    writeInt(header.fileLength)
}

class WaveHeader {
    var format: Format = Format.PCM
    var channel: Channel = Channel.Mono
    var fileLength = 0
    var sampleRate = 16000

    class Builder {
        private val header = WaveHeader()

        fun setFormat(value: Format) = apply {
            header.format = value
        }

        fun setChannel(value: Channel) = apply {
            header.channel = value
        }

        fun setFileLength(value: Int) = apply {
            header.fileLength = value
        }

        fun setSampleRate(value: Int) = apply {
            header.sampleRate = value
        }

        fun build(): WaveHeader = header
    }
}

sealed class Channel {
    abstract val count: Short

    object Mono : Channel() {
        override val count: Short = 1
    }

    object Stereo : Channel() {
        override val count: Short = 2
    }
}

sealed class Format {
    abstract val id: Short
    abstract val bitPerSample: Short


    object PCM : Format() {
        override val id: Short = 1
        override val bitPerSample: Short = 16
    }

    object ALAW : Format() {
        override val id: Short = 6
        override val bitPerSample: Short = 8
    }

    object ULAW : Format() {
        override val id: Short = 7
        override val bitPerSample: Short = 8
    }
}