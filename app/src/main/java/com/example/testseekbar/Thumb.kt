package com.example.testseekbar

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable

class Thumb(val drawable: Drawable?, val thumbBound: Rect, val thumbDrawableSize: Size = Size()) {

    fun screenSizeChanged(newHeight: Int) {
        thumbBound.top = ((newHeight - thumbDrawableSize.height) / 2)
        thumbBound.bottom = (newHeight - thumbBound.top)
    }

    fun draw(canvas: Canvas) {
        drawable?.bounds = thumbBound
        drawable?.draw(canvas)
    }
}