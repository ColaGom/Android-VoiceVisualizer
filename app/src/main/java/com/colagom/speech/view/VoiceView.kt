package com.colagom.speech.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

abstract class VoiceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let(::render)
    }

    protected fun dpToPx(dp: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    )

    protected fun px(dp: Float): Lazy<Float> = lazy {
        dpToPx(dp)
    }

    protected fun px(dp: Int) = px(dp.toFloat())

    var amplitudes: List<Double> = mutableListOf()
        set(value) {
            field = value
            invalidate()
        }

    abstract fun render(canvas: Canvas)
}


