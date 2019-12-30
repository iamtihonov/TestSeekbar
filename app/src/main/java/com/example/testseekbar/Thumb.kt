package com.example.testseekbar

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.MotionEvent

class Thumb(private val drawable: Drawable?, val bound: Rect) {

    val width: Int = drawable?.intrinsicWidth ?: 0
    val halfWidth: Int = width / 2
    private val height: Int = drawable?.intrinsicHeight ?: 0

    fun screenSizeChanged(newHeight: Int) {
        bound.top = ((newHeight - height) / 2)
        bound.bottom = (newHeight - bound.top)
    }

    fun contains(e: MotionEvent): Boolean {
        return bound.contains(e.x.toInt(), e.y.toInt())
    }

    fun updateLeftPosition(value: Int) {
        bound.left = value
        bound.right = value + width
    }

    fun draw(canvas: Canvas) {
        drawable?.bounds = bound
        drawable?.draw(canvas)
    }
}