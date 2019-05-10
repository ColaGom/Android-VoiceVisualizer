package com.colagom.speech

import android.util.Log

class VoiceRecorder(override val listener: Recorder.Listener) : Recorder {
    private val record by lazy {
        VoiceRecord()
    }

    private var buffer: ByteArray = byteArrayOf()

    override var state: Recorder.State = Recorder.State.WAIT

    override fun start() {
        if (state != Recorder.State.WAIT) return

        state = Recorder.State.RECORDING
        debug("start recording")
        buffer = ByteArray(record.minBufferSize)

        Thread(Runnable {
            while (state == Recorder.State.RECORDING) {
                val length = record.read(buffer, 0, buffer.size)
                if (length != 0) {
                    listener.onRecorded(buffer)
                }
            }
        }).start()

        debug("stop recording")
        record.release()
    }

    override fun stop() {
        state = Recorder.State.STOP
    }
}


fun debug(log: String) {
    Log.d("VoiceRecorder", log)
}