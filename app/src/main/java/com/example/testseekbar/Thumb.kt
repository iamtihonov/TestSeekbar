package com.example.testseekbar

import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.core.graphics.toRect

class Thumb(private val drawable: Drawable?, val bound: RectF) {

    val width: Int = drawable?.intrinsicWidth ?: 0
    val halfWidth: Int = width / 2
    private val height: Int = drawable?.intrinsicHeight ?: 0

    fun screenSizeChanged(newHeight: Int) {
        bound.top = ((newHeight - height) / 2).toFloat()
        bound.bottom = (newHeight - bound.top)
    }

    fun contains(e: MotionEvent): Boolean {
        return bound.contains(e.x, e.y)
    }

    fun updateLeftPosition(value: Float) {
        bound.left = value
        bound.right = value + width
    }

    fun draw(canvas: Canvas) {
        drawable?.bounds = bound.toRect()
        drawable?.draw(canvas)
    }
}