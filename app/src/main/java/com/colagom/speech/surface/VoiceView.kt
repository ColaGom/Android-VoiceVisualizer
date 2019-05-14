package com.colagom.speech.surface

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.colagom.speech.VoiceRecord
import kotlin.math.max
import kotlin.math.min

class VoiceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

    fun setVoiceContext(voiceContext: VoiceContext) {
        renderer = VoiceRenderer(voiceContext)
        holder.addCallback(renderer)
    }

    private lateinit var renderer: VoiceRenderer
}

interface VoiceContext {
    val amplitudes: MutableList<Double>
    var audioSource: ByteArray
    var audioLength: Int
    val res: Resources
    val config: VoiceRecord.Config
}

class VoiceRenderer(private val voiceContext: VoiceContext) : Runnable, SurfaceHolder.Callback {

    private var isRendering = false
    private lateinit var holder: SurfaceHolder

    private fun start(holder: SurfaceHolder) {
        if (isRendering) return
        isRendering = true
        this@VoiceRenderer.holder = holder
        Thread(this).apply {
            priority = Thread.MAX_PRIORITY
        }.start()
    }

    private fun stop() {
        if (!isRendering) return
        isRendering = false
    }

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
        }
    }
    private val paintDivider by lazy {
        Paint().apply {
            color = colorDivider
            strokeWidth = this@VoiceRenderer.strokeWidth
            style = Paint.Style.STROKE
        }
    }
    private val paintDashLine by lazy {
        Paint().apply {
            color = colorHighlight
            strokeWidth = this@VoiceRenderer.strokeWidth / 2
            pathEffect = DashPathEffect(dashInterval, 0f)
        }
    }
    private val dashInterval by lazy {
        floatArrayOf(dpToPx(2f), dpToPx(1f))
    }

    private fun dpToPx(dp: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        voiceContext.res.displayMetrics
    )

    private fun px(dp: Float): Lazy<Float> = lazy {
        dpToPx(dp)
    }

    private fun px(dp: Int) = px(dp.toFloat())

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

    private fun render(canvas: Canvas) {
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        val center = height / 2
        val amplitudeLimit = center * maxAmplitude

        canvas.drawColor(colorBackground)
        canvas.drawLine(padding, center, width - padding, center, paintDivider)

        canvas.drawDashLine(minHolder, center - amplitudeLimit)
        canvas.drawDashLine(maxHolder, center + amplitudeLimit)

        val cx = width - padding

        //margin : stroke = 1 : 3
        val displayMinBarCount = 50
        val displayMaxBarCount = 50

        val data = voiceContext.amplitudes

        val margin = (width - padding * 2) / max(
            min(displayMaxBarCount, data.size),
            displayMinBarCount
        ) / 4

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

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        holder?.let {
            this@VoiceRenderer.holder = it
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        holder?.let(::start)
    }

    override fun run() {
        while (isRendering) {
            holder.lockCanvas()?.let { canvas ->
                try {
                    synchronized(this@VoiceRenderer) {
                        render(canvas)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    stop()
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}

