package com.colagom.speech.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet


class VoiceDotView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VoiceView(context, attrs, defStyleAttr) {
    private val colorRed by lazy {
        Color.rgb(237, 86, 50)
    }
    private val colorGreen by lazy {
        Color.rgb(237, 86, 50)
    }
    private val colorGray by lazy {
        Color.rgb(216, 216, 216)
    }

    private val paintBlock by lazy {
        Paint().apply {
            style = Paint.Style.FILL
        }
    }

    private val margin by px(2)

    private val wc = 4
    private val hc = 7
    private val maxAmplitude = 0.6

    override fun render(canvas: Canvas) = with(canvas) {
        if (amplitudes.isEmpty()) return
        val blockWidth = (width - margin * (wc - 1)) / wc
        val blockHeight = blockWidth
        val lastIdx = amplitudes.lastIndex
        val amplitudeStep = 1.0 / hc

        for (x in lastIdx downTo lastIdx - wc) {
            val amplitude = if (x < 0) 0.0 else amplitudes[x]
            val aColor = if (amplitude > maxAmplitude) colorRed else colorGreen
            val disX = lastIdx - x

            for (y in hc downTo 1) {
                val left = width - (blockWidth * (disX + 1)) - margin * disX
                val top = (y * blockHeight) + (margin * (y - 1))
                paintBlock.color = if (amplitude > amplitudeStep * y) aColor else colorGray

                canvas.drawRect(left, top, left + blockWidth, top + blockHeight, paintBlock)
            }
        }
    }
}