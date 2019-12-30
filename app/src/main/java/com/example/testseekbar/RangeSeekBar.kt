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
    private var leftThumbDrawable: Drawable? = null
    private var rigthThumbDrawable: Drawable? = null
    private var activeLineColor: Int = 0
    private var defaultLineColor: Int = 0
    private var lineHeight: Int = 0
    private var startRangleValue = 0
    private var endRangeValue = 100
    private var thumbDrawableSize = Size()
    private var horizontalScrollPosition = 0
    private var leftThumbBound = Rect()
    private var leftThumbTouchBound = Rect()
    private var rightThumbBound = Rect()
    private var rightThumbTouchBound = Rect()
    private var touchPadding = 0
    private var seekBarPadding = 0

    init {
        context?.apply {
            leftThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
            rigthThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
            activeLineColor = ContextCompat.getColor(this, R.color.blue_52_172_250)
            defaultLineColor = ContextCompat.getColor(this, R.color.grey_229_232_237)
            lineHeight = resources.getDimensionPixelSize(R.dimen.size_1dp)
            touchPadding = resources.getDimensionPixelSize(R.dimen.size_5dp)
            seekBarPadding = resources.getDimensionPixelSize(R.dimen.size_22dp)
        }

        val width = leftThumbDrawable?.intrinsicWidth ?: 0
        val height = leftThumbDrawable?.intrinsicHeight ?: 0
        thumbDrawableSize = Size(width, height)
        leftThumbBound.left = seekBarPadding
        leftThumbBound.right = leftThumbBound.left + thumbDrawableSize.width
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(newWidth, newHeight, oldw, oldh)
        screenHeight = newHeight.toFloat()
        leftThumbBound.top = ((screenHeight - thumbDrawableSize.height) / 2).toInt()
        leftThumbBound.bottom = (screenHeight - leftThumbBound.top).toInt()
        leftThumbTouchBound.top = leftThumbBound.top + touchPadding
        leftThumbTouchBound.bottom = leftThumbBound.bottom + touchPadding

        rightThumbBound.right = newWidth - seekBarPadding
        rightThumbBound.left = rightThumbBound.right - thumbDrawableSize.width
        rightThumbBound.top = leftThumbBound.top
        rightThumbBound.bottom = leftThumbBound.bottom
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val usingCanvas = canvas ?: return

        leftThumbDrawable?.bounds = leftThumbBound
        leftThumbDrawable?.draw(usingCanvas)

        rigthThumbDrawable?.bounds = rightThumbBound
        rigthThumbDrawable?.draw(usingCanvas)
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        private var clickLeftThumb = false
        private var clickRightThumb = false

        override fun onDown(e: MotionEvent): Boolean {
            clickLeftThumb = leftThumbBound.contains(e.x.toInt(), e.y.toInt())
            clickRightThumb = rightThumbBound.contains(e.x.toInt(), e.y.toInt())
            Log.d("testScroll", "onScroll() contains = $clickLeftThumb")
            return clickLeftThumb
        }

        @Suppress("CascadeIf")
        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
            distanceY: Float
        ): Boolean {
            val it = e2 ?: return true
            if (clickLeftThumb) {
                leftThumbBound.left = if (it.x <= thumbDrawableSize.width / 2 + seekBarPadding) {
                    seekBarPadding
                } else if (it.x >= width - thumbDrawableSize.width / 2 - seekBarPadding) {
                    width - thumbDrawableSize.width - seekBarPadding
                } else {
                    (it.x - thumbDrawableSize.width / 2).toInt()
                }
                leftThumbBound.right = leftThumbBound.left + thumbDrawableSize.width
                invalidate()
            } else if (clickRightThumb) {
                rightThumbBound.left = if (it.x <= thumbDrawableSize.width / 2 + seekBarPadding) {
                    seekBarPadding
                } else if (it.x >= width - thumbDrawableSize.width / 2 - seekBarPadding) {
                    width - thumbDrawableSize.width - seekBarPadding
                } else {
                    (it.x - thumbDrawableSize.width / 2).toInt()
                }
                rightThumbBound.right = rightThumbBound.left + thumbDrawableSize.width
                invalidate()
            }

            Log.d("testScroll", "onScroll() x2 = ${e2.x}")

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