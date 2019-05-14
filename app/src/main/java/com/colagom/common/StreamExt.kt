package com.colagom.common

import java.io.FileOutputStream

fun FileOutputStream.writeString(value: String) {
    value.forEach {
        write(it.toInt())
    }
}

fun FileOutputStream.writeInt(value: Int) {
    write(value shr 0)
    write(value shr 8)
    write(value shr 16)
    write(value shr 24)
}

fun FileOutputStream.writeShort(value: Short) {
    write(value.toInt() shr 0)
    write(value.toInt() shr 8)
}

fun FileOutputStream.writeWaveHeader(header: WaveHeader) = with(header) {
    writeString("RIFF")
    writeInt(36 + fileLength)
    writeString("WAVE")

    writeString("fmt ")
    writeInt(16)
    writeShort(format.id)
    writeShort(channel.count)
    writeInt(sampleRate)

    val blockAlign = (channel.count * format.bitPerSample / 8).toShort()
    writeInt(blockAlign * sampleRate)
    writeShort(blockAlign)
    writeShort(format.bitPerSample)

    writeString("data")
    writeInt(header.fileLength)
}