package com.colagom.speech.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

class VoiceBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VoiceView(context, attrs, defStyleAttr) {

    private val maxVisibleCount = 50

    private val paintBarNormal by lazy {
        Paint().apply {
            color = colorWhite
            style = Paint.Style.STROKE
        }
    }

    private val paintBarHighlight by lazy {
        Paint().apply {
            color = colorHighlight
            style = Paint.Style.STROKE
        }
    }

    private val paintFont by lazy {
        Paint().apply {
            typeface = Typeface.DEFAULT
            color = colorHighlight
            textSize = dpToPx(13f)
        }
    }
    private val paintDivider by lazy {
        Paint().apply {
            color = colorDivider
            strokeWidth = this@VoiceBarView.strokeWidth
            style = Paint.Style.STROKE
        }
    }
    private val paintDashLine by lazy {
        Paint().apply {
            color = colorHighlight
            strokeWidth = this@VoiceBarView.strokeWidth / 2
            pathEffect = DashPathEffect(dashInterval, 0f)
        }
    }
    private val dashInterval by lazy {
        floatArrayOf(dpToPx(2f), dpToPx(1f))
    }

    private val strokeWidth by px(2)
    private val padding by px(12)

    private val maxHolder = "max"
    private val minHolder = "min"
    private val maxAmplitude = 0.7f

    private val colorDivider by lazy {
        Color.rgb(170, 170, 170)
    }
    private val colorBackground by lazy {
        Color.rgb(32, 32, 32)
    }
    private val colorHighlight by lazy {
        Color.rgb(237, 86, 50)
    }
    private val colorWhite by lazy {
        Color.rgb(216, 216, 216)
    }

    private fun Canvas.drawDashLine(holder: String, sy: Float) {
        drawText(holder, padding, sy, paintFont)
        drawLine(padding, sy, width - padding, sy, paintDashLine)
    }

    override fun render(canvas: Canvas) {
        if (amplitudes.isEmpty()) return
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        val center = height / 2
        val amplitudeLimit = center * maxAmplitude

        canvas.drawColor(colorBackground)
        canvas.drawLine(padding, center, width - padding, center, paintDivider)

        canvas.drawDashLine(minHolder, center - amplitudeLimit)
        canvas.drawDashLine(maxHolder, center + amplitudeLimit)

        val cx = width - padding

        val data = amplitudes

        val margin = (width - padding * 2) / maxVisibleCount / 4
        val barStrokeWidth = margin * 3

        paintBarHighlight.strokeWidth = barStrokeWidth
        paintBarNormal.strokeWidth = barStrokeWidth

        for (idx in data.lastIndex downTo 0) {
            val x = cx - (data.lastIndex - idx) * (margin + barStrokeWidth)
            if (x < padding) break
            val amplitude = data[idx]
            val barHeight = height * amplitude
            val sy = center - barHeight / 2
            val paint = if (amplitude < maxAmplitude) paintBarNormal else paintBarHighlight

            canvas.drawLine(x, sy.toFloat(), x, (sy + barHeight).toFloat(), paint)
        }
    }
}