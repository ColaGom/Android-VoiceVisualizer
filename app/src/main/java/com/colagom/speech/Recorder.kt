package com.colagom.speech

interface Recorder {
    var state: State
    val listener: Listener

    fun start()
    fun stop()

    interface Listener {
        fun onRecorded(buffer: ByteArray)
        fun onFinisehdRecord()
    }

    enum class State {
        WAIT,
        RECORDING,
        STOP
    }
}