package com.example.movie_db

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MoviesViewPager : ViewPager {
    private var canSwipeDown = false

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        canSwipeDown = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (canSwipeDown) super.onInterceptTouchEvent(ev) else false
    }

    fun setSwiping(swipe: Boolean) {
        canSwipeDown = swipe
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (canSwipeDown) {
            super.onTouchEvent(event)
        } else false
    }
}
