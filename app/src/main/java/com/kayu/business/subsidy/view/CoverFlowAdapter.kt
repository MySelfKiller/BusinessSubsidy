package com.kayu.business.subsidy.view

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.ViewGroup
import android.view.View

class CoverFlowAdapter(
    /**
     * 子元素的集合
     */
    private val mViewList: List<View>?, private val mContext: Context
) : PagerAdapter(), ViewPager.OnPageChangeListener {
    /**
     * 滑动监听的回调接口
     */
    private var listener: OnPageSelectListener? = null
    private var mPositionOffset = 0f
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mViewList!![position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = mViewList!![position]
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return mViewList?.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        // 该方法回调ViewPager 的滑动偏移量
        if (mViewList!!.size > 0 && position < mViewList.size) {
            //当前手指触摸滑动的页面,从0页滑动到1页 offset越来越大，padding越来越大
            mViewList[position].scaleX = 1 - positionOffset * 0.17f
            mViewList[position].scaleY = 1 - positionOffset * 0.17f
            mViewList[position].alpha = 1 - positionOffset * 0.5f
            //            mViewList.get(position).setTranslationX(dp2px(60) * positionOffset);

            // position+1 为即将显示的页面，越来越大
            if (position < mViewList.size - 1) {
                mViewList[position + 1].scaleX = 0.83f + positionOffset * 0.17f
                mViewList[position + 1].scaleY = 0.83f + positionOffset * 0.17f
                mViewList[position + 1].alpha = 0.5f + positionOffset * 0.5f
            }
        }
        mPositionOffset = positionOffset
    }

    override fun onPageSelected(position: Int) {
        // 回调选择的接口
        if (listener != null) {
            listener!!.select(position)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    /**
     * 当将某一个作为最中央时的回调
     *
     * @param listener
     */
    fun setOnPageSelectListener(listener: OnPageSelectListener?) {
        this.listener = listener
    }
}