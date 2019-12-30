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
    private var leftThumb: Thumb
    private var rightThumb: Thumb

    private var screenHeight = 0f
    private var activeLineColor = 0
    private var defaultLineColor = 0
    private var lineHeight = 0
    private var touchPadding = 0
    private var seekBarPadding = 0

    init {
        var leftThumbDrawable: Drawable? = null
        var rightThumbDrawable: Drawable? = null
        context?.apply {
            leftThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
            rightThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
            activeLineColor = ContextCompat.getColor(this, R.color.blue_52_172_250)
            defaultLineColor = ContextCompat.getColor(this, R.color.grey_229_232_237)
            lineHeight = resources.getDimensionPixelSize(R.dimen.size_1dp)
            touchPadding = resources.getDimensionPixelSize(R.dimen.size_5dp)
            seekBarPadding = resources.getDimensionPixelSize(R.dimen.size_22dp)
        }

        val width = leftThumbDrawable?.intrinsicWidth ?: 0
        val height = leftThumbDrawable?.intrinsicHeight ?: 0
        val thumbDrawableSize = Size(width, height)

        val leftThumbBound = Rect()
        leftThumbBound.left = seekBarPadding
        leftThumbBound.right = leftThumbBound.left + thumbDrawableSize.width

        val rightThumbBound = Rect()
        leftThumb = Thumb(leftThumbDrawable, leftThumbBound, thumbDrawableSize)
        rightThumb = Thumb(rightThumbDrawable, rightThumbBound, thumbDrawableSize)
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(newWidth, newHeight, oldw, oldh)
        screenHeight = newHeight.toFloat()
        leftThumb.screenSizeChanged(newHeight)
        rightThumb.screenSizeChanged(newHeight)

        rightThumb.thumbBound.right = newWidth - seekBarPadding
        rightThumb.thumbBound.left = rightThumb.thumbBound.right -
                rightThumb.thumbDrawableSize.width
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val usingCanvas = canvas ?: return

        leftThumb.draw(usingCanvas)
        rightThumb.draw(usingCanvas)
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        private var clickLeftThumb = false
        private var clickRightThumb = false

        override fun onDown(e: MotionEvent): Boolean {
            clickLeftThumb = leftThumb.thumbBound.contains(e.x.toInt(), e.y.toInt())
            clickRightThumb = rightThumb.thumbBound.contains(e.x.toInt(), e.y.toInt())
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
                val leftBorder = leftThumb.thumbDrawableSize.width / 2 + seekBarPadding
                val rightBorder = width - leftThumb.thumbDrawableSize.width / 2 - seekBarPadding
                leftThumb.thumbBound.left = if (it.x <= leftBorder) {
                    seekBarPadding
                } else if (it.x >= rightBorder) {
                    width - leftThumb.thumbDrawableSize.width - seekBarPadding
                } else {
                    (it.x - leftThumb.thumbDrawableSize.width / 2).toInt()
                }
                leftThumb.thumbBound.right = leftThumb.thumbBound.left + leftThumb.thumbDrawableSize.width
                invalidate()
            } else if (clickRightThumb) {
                rightThumb.thumbBound.left = if (it.x <= leftThumb.thumbDrawableSize.width / 2 + seekBarPadding) {
                    seekBarPadding
                } else if (it.x >= width - leftThumb.thumbDrawableSize.width / 2 - seekBarPadding) {
                    width - leftThumb.thumbDrawableSize.width - seekBarPadding
                } else {
                    (it.x - leftThumb.thumbDrawableSize.width / 2).toInt()
                }
                rightThumb.thumbBound.right = rightThumb.thumbBound.left + leftThumb.thumbDrawableSize.width
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