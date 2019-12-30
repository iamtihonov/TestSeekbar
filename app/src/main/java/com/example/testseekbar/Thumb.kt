package com.example.testseekbar

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable

class Thumb(private val drawable: Drawable?, val bound: Rect) {

    val width: Int = drawable?.intrinsicWidth ?: 0
    private val height: Int = drawable?.intrinsicHeight ?: 0
    val halfWidth: Int

    init {
        halfWidth = width / 2
    }

    fun screenSizeChanged(newHeight: Int) {
        bound.top = ((newHeight - height) / 2)
        bound.bottom = (newHeight - bound.top)
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