package com.example.testseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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
    private val testPaint = Paint()

    private var screenHeight = 0f
    private var activeLineColor = 0
    private var defaultLineColor = 0
    private var lineHeight = 0
    private var touchPadding = 0
    private var seekBarPadding = 0
    private var leftBorder = 0
    private var rightBorder = 0

    init {
        context?.apply {
            activeLineColor = ContextCompat.getColor(this, R.color.blue_52_172_250)
            defaultLineColor = ContextCompat.getColor(this, R.color.grey_229_232_237)
            lineHeight = resources.getDimensionPixelSize(R.dimen.size_1dp)
            touchPadding = resources.getDimensionPixelSize(R.dimen.size_5dp)
            seekBarPadding = resources.getDimensionPixelSize(R.dimen.size_22dp)
        }

        val leftThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
        val rightThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)

        val leftThumbBound = Rect()
        leftThumbBound.left = seekBarPadding
        leftThumb = Thumb(leftThumbDrawable, leftThumbBound)
        rightThumb = Thumb(rightThumbDrawable, Rect())

        leftThumbBound.right = leftThumbBound.left + leftThumb.width
        testPaint.color = Color.RED
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(newWidth, newHeight, oldw, oldh)
        screenHeight = newHeight.toFloat()
        leftThumb.screenSizeChanged(newHeight)
        rightThumb.screenSizeChanged(newHeight)

        rightThumb.bound.right = newWidth - seekBarPadding
        rightThumb.bound.left = rightThumb.bound.right - rightThumb.width

        leftBorder = seekBarPadding
        rightBorder = newWidth - seekBarPadding
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val usingCanvas = canvas ?: return

        usingCanvas.drawLine(leftBorder.toFloat(), 0.0f, leftBorder.toFloat(),
            height.toFloat(), testPaint)
        usingCanvas.drawLine(rightBorder.toFloat(), 0.0f, rightBorder.toFloat(),
            height.toFloat(), testPaint)
        leftThumb.draw(usingCanvas)
        rightThumb.draw(usingCanvas)
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        private var clickLeftThumb = false
        private var clickRightThumb = false

        override fun onDown(e: MotionEvent): Boolean {
            clickLeftThumb = leftThumb.contains(e)
            clickRightThumb = rightThumb.contains(e)
            Log.d("testScroll", "onScroll() contains = $clickLeftThumb")
            return clickLeftThumb
        }

        @Suppress("CascadeIf")
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
                              distanceY: Float): Boolean {
            val it = e2 ?: return true
            if (clickLeftThumb) {
                val leftPosition = getLeftPosition(it)
                leftThumb.updateLeftPosition(leftPosition)
                invalidate()
            } else if (clickRightThumb) {
                //leftBorder = max(leftBorder, leftThumb.bound.right - leftThumb.halfWidth)
                val leftPosition = getLeftPosition(it)
                rightThumb.updateLeftPosition(leftPosition)
                invalidate()
            }

            Log.d("testScroll", "onScroll() x2 = ${e2.x}")
            return true
        }
    }

    @Suppress("CascadeIf")
    private fun getLeftPosition(it: MotionEvent): Int {
        val halfWidth = leftThumb.halfWidth
        return if (it.x <= leftBorder + halfWidth) {
            leftBorder
        } else if (it.x >= rightBorder - halfWidth) {
            rightBorder - leftThumb.width
        } else {
            (it.x - halfWidth).toInt()
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