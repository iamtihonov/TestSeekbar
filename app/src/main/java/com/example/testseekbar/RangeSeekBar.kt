package com.example.testseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * Прогресс бар (использоватся должен горизонтальный) выше которого рисуются 4 белые
 * разделительные линии
 */
class RangeSeekBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var screenHeight = 0f
    private var thumbDrawable: Drawable? = null
    private var activeLineColor: Int = 0
    private var defaultLineColor: Int = 0
    private var lineHeight: Int = 0
    private var startRangleValue = 0
    private var endRangeValue = 100
    private var thumbDrawableSize = Size()
    private var horizontalScrollPosition = 0
    private var thumbBound = Rect()

    init {
        context?.apply {
            thumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
            activeLineColor = ContextCompat.getColor(this, R.color.blue_52_172_250)
            defaultLineColor = ContextCompat.getColor(this, R.color.grey_229_232_237)
            lineHeight = resources.getDimensionPixelSize(R.dimen.size_1dp)
        }

        val width = thumbDrawable?.intrinsicWidth ?: 0
        val height =  thumbDrawable?.intrinsicHeight ?: 0
        thumbDrawableSize = Size(width, height)
        thumbBound.left = 0
        thumbBound.right = thumbBound.left + thumbDrawableSize.width
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenHeight = h.toFloat()
        thumbBound.top = ((screenHeight - thumbDrawableSize.height) / 2).toInt()
        thumbBound.bottom = (screenHeight - thumbBound.top).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val usingCanvas = canvas ?: return

        thumbDrawable?.bounds = thumbBound
        thumbDrawable?.draw(usingCanvas)
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        @Suppress("CascadeIf")
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
                              distanceY: Float): Boolean {
            Log.d("testScroll", "onScroll() x1 = ${e1?.x}")
            Log.d("testScroll", "onScroll() x2 = ${e2?.x}")
            e2?.let {
                thumbBound.left = if(it.x <= thumbDrawableSize.width / 2) {
                    0
                } else if(it.x >= width - thumbDrawableSize.width / 2) {
                    width - thumbDrawableSize.width
                } else {
                    (it.x - thumbDrawableSize.width / 2).toInt()
                }
                thumbBound.right = thumbBound.left + thumbDrawableSize.width
                invalidate()
            }

            return true
        }
    }

    private val detector = GestureDetector(context, scrollListener)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event).let {
            true
        }
    }
}