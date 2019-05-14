package com.colagom.speech

import android.util.Log

class VoiceRecorder(private val config: VoiceRecord.Config, override val listener: Recorder.Listener) : Recorder {
    override var state: Recorder.State = Recorder.State.WAIT

    override fun start() {
        if (state != Recorder.State.WAIT) return

        state = Recorder.State.RECORDING
        debug("start recording")

        val record = VoiceRecord()
        record.startRecording()

        Thread(Runnable {
            val buffer = ByteArray(config.bufferSize)

            while (state == Recorder.State.RECORDING) {
                val length = record.read(buffer, 0, buffer.size)

                if (length > 0) {
                    listener.onRecorded(buffer)
                }
            }
            debug("stop recording")
            record.stop()
            record.release()
            listener.onFinisehdRecord()
        }).start()
    }

    override fun stop() {
        state = Recorder.State.STOP
    }
}

fun debug(log: String) {
    Log.d("VoiceRecorder", log)
}