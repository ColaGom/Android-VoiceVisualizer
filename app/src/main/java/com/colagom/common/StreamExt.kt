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