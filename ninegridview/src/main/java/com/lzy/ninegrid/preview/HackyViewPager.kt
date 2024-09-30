package com.lzy.ninegrid.preview

import androidx.viewpager.widget.ViewPager
import android.view.MotionEvent
import android.content.Context
import android.util.AttributeSet
import java.lang.IllegalArgumentException

/** 修复图片在ViewPager控件中缩放报错的BUG  */
class HackyViewPager : ViewPager {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }
}