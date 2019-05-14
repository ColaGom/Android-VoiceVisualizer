package com.colagom.voicevisualizer

import android.Manifest
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.colagom.common.writeWaveHeader
import com.colagom.speech.Recorder
import com.colagom.speech.VoiceRecord
import com.colagom.speech.VoiceRecorder
import com.colagom.speech.surface.VoiceContext
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN


class MainActivity : AppCompatActivity(), Recorder.Listener, VoiceContext {

    override val config: VoiceRecord.Config = VoiceRecord.Config.Default
    override val amplitudes: MutableList<Double> = mutableListOf()
    private val MAX_LENGTH = 1024 * 1024 * 10 / 2
    override var audioSource = ByteArray(MAX_LENGTH)
    override var audioLength: Int = 0
    override val res: Resources
        get() = resources

    override fun onRecorded(buffer: ByteArray) {
        buffer.copyInto(audioSource, audioLength, 0, buffer.size)
        audioLength += buffer.size
        var maxAmplitude = 0.0

        ByteBuffer.wrap(buffer).order(LITTLE_ENDIAN).let {
            while (it.hasRemaining()) {
                val amplitude = it.short.toDouble() / Short.MAX_VALUE
                if (maxAmplitude < amplitude) {
                    maxAmplitude = amplitude
                }
            }
        }

        amplitudes.add(maxAmplitude)
        vv.amplitudes = amplitudes
        vdv.amplitudes = amplitudes
    }

    override fun onFinisehdRecord() {
        if (audioSource.isNotEmpty()) {
            //save to File
            val saveFile = File(Environment.getExternalStorageDirectory().path + "/trReqAudio.wav")
            if (saveFile.exists())
                saveFile.delete()

            val header = config.toWaveHeader(audioLength)

            saveFile.outputStream().run {
                writeWaveHeader(header)

                write(audioSource.copyOfRange(0, audioLength))
                close()
            }
        }
    }

    private val REQUEST_PERMISION = 1

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission_group.STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var voiceRecorder: VoiceRecorder? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISION)

        val config = VoiceRecord.Config.Default

        btn_record.setOnClickListener {
            isRecording = !isRecording

            if (isRecording) {
                audioLength = 0
                voiceRecorder = VoiceRecorder(config, this).also { it.start() }
            } else {
                voiceRecorder?.stop()
                voiceRecorder = null
            }

            btn_record.text = if (isRecording) "STOP" else "RECORD"
        }
    }
}