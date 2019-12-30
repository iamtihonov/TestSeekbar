package com.example.testseekbar

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable

class Thumb(val drawable: Drawable?, val bound: Rect) {

    val width: Int
    val height: Int

    init {
        width = drawable?.intrinsicWidth ?: 0
        height = drawable?.intrinsicHeight ?: 0
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