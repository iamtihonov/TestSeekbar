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
    private var leftThumbBound = Rect()
    private var leftThumbTouchBound = Rect()
    private var touchPadding = 0
    private var seekBarPadding = 0

    init {
        context?.apply {
            thumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
            activeLineColor = ContextCompat.getColor(this, R.color.blue_52_172_250)
            defaultLineColor = ContextCompat.getColor(this, R.color.grey_229_232_237)
            lineHeight = resources.getDimensionPixelSize(R.dimen.size_1dp)
            touchPadding = resources.getDimensionPixelSize(R.dimen.size_5dp)
            seekBarPadding = resources.getDimensionPixelSize(R.dimen.size_22dp)
        }

        val width = thumbDrawable?.intrinsicWidth ?: 0
        val height =  thumbDrawable?.intrinsicHeight ?: 0
        thumbDrawableSize = Size(width, height)
        leftThumbBound.left = seekBarPadding
        leftThumbBound.right = leftThumbBound.left + thumbDrawableSize.width
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenHeight = h.toFloat()
        leftThumbBound.top = ((screenHeight - thumbDrawableSize.height) / 2).toInt()
        leftThumbBound.bottom = (screenHeight - leftThumbBound.top).toInt()
        leftThumbTouchBound.top = leftThumbBound.top + touchPadding
        leftThumbTouchBound.bottom = leftThumbBound.bottom + touchPadding
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val usingCanvas = canvas ?: return

        thumbDrawable?.bounds = leftThumbBound
        thumbDrawable?.draw(usingCanvas)
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        private var downContains = false

        override fun onDown(e: MotionEvent): Boolean {
            downContains = leftThumbBound.contains(e.x.toInt(), e.y.toInt())
            Log.d("testScroll", "onScroll() contains = $downContains")
            return downContains
        }

        @Suppress("CascadeIf")
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
                              distanceY: Float): Boolean {
            if(!downContains) {
                return false
            }

            Log.d("testScroll", "onScroll() x2 = ${e2?.x}")
            e2?.let {
                leftThumbBound.left = if(it.x <= thumbDrawableSize.width / 2 + seekBarPadding) {
                    seekBarPadding
                } else if(it.x >= width - thumbDrawableSize.width / 2 - seekBarPadding) {
                    width - thumbDrawableSize.width - seekBarPadding
                } else {
                    (it.x - thumbDrawableSize.width / 2).toInt()
                }
                leftThumbBound.right = leftThumbBound.left + thumbDrawableSize.width
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