package android.view.timestable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.sin

class TimesTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
    }

    private val numberOfPoints = 200

    var times = MIN_TIMES
        set(value) {
            field = value
            invalidate()
        }

    private val minColorValue = 128
    private val maxColorValue = 254

    private val valueAnimator = ValueAnimator.ofFloat(2.0f, 100.0f).apply {
        duration = (1000L * MAX_TIMES * 2.0f).toLong()
        addUpdateListener {
            times = it.animatedValue as Float
            timesValueCallback?.onTimesValueUpdated(times)
            invalidate()
        }
        addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                timesValueCallback?.onTimesValueAnimationStarted()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                timesValueCallback?.onTimesValueAnimationEnded()
            }
        })
    }

    private var timesValueCallback: TimesValueCallback? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelAnimation()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val radius = width * 0.5f
            val cX = radius
            val cy = height * 0.5f
            val red: Int = ThreadLocalRandom.current().nextInt(minColorValue, maxColorValue + 1)
            val green: Int = ThreadLocalRandom.current().nextInt(minColorValue, maxColorValue + 1)
            val blue: Int = ThreadLocalRandom.current().nextInt(minColorValue, maxColorValue + 1)
            linePaint.setARGB(255, red, green, blue)
            val thetaDelta = 360.0f / numberOfPoints
            for (i in 0 until numberOfPoints) {
                val startDegree = i * thetaDelta
                val endDegree = times * i * thetaDelta
                val startX = cX + radius * cos(Math.toRadians(startDegree.toDouble())).toFloat()
                val startY = cy + radius * sin(Math.toRadians(startDegree.toDouble())).toFloat()
                val endX = cX + radius * cos(Math.toRadians(endDegree.toDouble())).toFloat()
                val endY = cy + radius * sin(Math.toRadians(endDegree.toDouble())).toFloat()
                it.drawLine(
                    startX,
                    startY,
                    endX,
                    endY,
                    linePaint
                )
            }
        }
    }

    fun setTimesValueCallback(timesValueCallback: TimesValueCallback) {
        this.timesValueCallback = timesValueCallback
    }

    fun isAnimationRunning() = valueAnimator.isRunning

    fun animateTimesValue() {
        if (!valueAnimator.isRunning) {
            valueAnimator.start()
        }
    }

    fun cancelAnimation() {
        if (valueAnimator.isRunning) {
            valueAnimator.cancel()
        }
    }

    interface TimesValueCallback {
        fun onTimesValueUpdated(times: Float)
        fun onTimesValueAnimationStarted()
        fun onTimesValueAnimationEnded()
    }

    private companion object {
        const val MIN_TIMES = 2.0F
        const val MAX_TIMES = 100.0F
    }
}