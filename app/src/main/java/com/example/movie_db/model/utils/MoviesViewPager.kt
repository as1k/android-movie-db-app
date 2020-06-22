package com.example.movie_db.model.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MoviesViewPager : ViewPager {

    private var swipeable = false

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        swipeable = true
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        return if (swipeable) super.onInterceptTouchEvent(motionEvent) else false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeable) {
            super.onTouchEvent(event)
        } else false
    }

    fun setSwiping(swipe: Boolean) {
        swipeable = swipe
    }
}
