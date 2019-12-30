package com.example.testseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
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
    private var defaultLinePaint = Paint()
    private var activeLinePaint = Paint()

    private var screenHeight = 0f
    private var lineHeight = 0
    private var touchPadding = 0
    private var seekBarPadding = 0
    private var leftBorder = 0.0f
    private var rightBorder = 0.0f
    private var lineTopPosition = 0.0f
    private var lineBottomPosition = 0.0f

    init {
        var activeLineColor = 0
        var defaultLineColor = 0

        context?.apply {
            activeLineColor = ContextCompat.getColor(this, R.color.blue_52_172_250)
            defaultLineColor = ContextCompat.getColor(this, R.color.grey_229_232_237)
        }

        val leftThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
        val rightThumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.test, null)
        lineHeight = resources.getDimensionPixelSize(R.dimen.size_3dp)
        touchPadding = resources.getDimensionPixelSize(R.dimen.size_5dp)
        seekBarPadding = resources.getDimensionPixelSize(R.dimen.size_22dp)

        val leftThumbBound = RectF()
        leftThumbBound.left = seekBarPadding.toFloat()
        leftThumb = Thumb(leftThumbDrawable, leftThumbBound)
        rightThumb = Thumb(rightThumbDrawable, RectF())

        leftThumbBound.right = leftThumbBound.left + leftThumb.width
        testPaint.color = Color.RED

        defaultLinePaint.color = defaultLineColor
        defaultLinePaint.style = Paint.Style.FILL

        activeLinePaint.color = activeLineColor
        activeLinePaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(newWidth, newHeight, oldw, oldh)
        screenHeight = newHeight.toFloat()

        leftThumb.screenSizeChanged(newHeight)
        rightThumb.screenSizeChanged(newHeight)

        rightThumb.bound.right = (newWidth - seekBarPadding).toFloat()
        rightThumb.bound.left = rightThumb.bound.right - rightThumb.width

        leftBorder = seekBarPadding.toFloat()
        rightBorder = (newWidth - seekBarPadding).toFloat()
        lineTopPosition = ((newHeight / 2) - (lineHeight / 2)).toFloat()
        lineBottomPosition = lineTopPosition + lineHeight
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val usingCanvas = canvas ?: return
        drawTestLines(usingCanvas)

        usingCanvas.drawRect(leftBorder, lineTopPosition, rightBorder, lineBottomPosition,
            defaultLinePaint)
        usingCanvas.drawRect(leftThumb.bound.left, lineTopPosition, rightThumb.bound.right,
            lineBottomPosition, activeLinePaint)

        leftThumb.draw(usingCanvas)
        rightThumb.draw(usingCanvas)
    }

    private fun drawTestLines(usingCanvas: Canvas) {
        usingCanvas.drawColor(Color.GREEN)
        usingCanvas.drawLine(leftBorder, 0.0f, leftBorder, height.toFloat(), testPaint)
        usingCanvas.drawLine(rightBorder, 0.0f, rightBorder, height.toFloat(), testPaint)
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
                val leftPosition = getLeftPosition(it, true)
                leftThumb.updateLeftPosition(leftPosition)
                invalidate()
            } else if (clickRightThumb) {
                val leftPosition = getLeftPosition(it, false)
                rightThumb.updateLeftPosition(leftPosition)
                invalidate()
            }

            Log.d("testScroll", "onScroll() x2 = ${e2.x}")
            return true
        }
    }

    @Suppress("CascadeIf")
    private fun getLeftPosition(it: MotionEvent, isLeft: Boolean): Float {
        val halfWidth = leftThumb.halfWidth
        val usingLeftBorder = if(isLeft) {//С учетом текущего положения второй перделки
            leftBorder
        } else {
            leftThumb.bound.right
        }

        val usingRightBorder = if(isLeft) {//С учетом текущего положения второй перделки
            rightThumb.bound.left
        } else {
            rightBorder
        }

        return if (it.x <= usingLeftBorder + halfWidth) {
            usingLeftBorder
        } else if (it.x >= usingRightBorder - halfWidth) {
            usingRightBorder - leftThumb.width
        } else {
            it.x - halfWidth
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