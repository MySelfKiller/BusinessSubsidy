package com.kayu.business.subsidy.view

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import android.widget.RelativeLayout
import com.kayu.business.subsidy.R
import android.view.View
import java.util.ArrayList

class CoverFlowViewPager(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs),
    OnPageSelectListener {
    private var mAdapter: CoverFlowAdapter? = null
    private val mViewPager: ViewPager

    //需要显示的视图集合,也就是每个Pager所要展示的View
    private val mViewList: MutableList<View> = ArrayList()
    private var listener: OnPageSelectListener? = null

    init {
        inflate(context, R.layout.widget_cover_flow, this)
        mViewPager = findViewById<View>(R.id.vp_conver_flow) as ViewPager
        init()
    }

    //初始化方法
    private fun init() {
        // 构造适配器，传入数据源
        mAdapter = CoverFlowAdapter(mViewList, context)
        // 设置选中的回调
        mAdapter!!.setOnPageSelectListener(this)
        // 设置适配器
        mViewPager.adapter = mAdapter
        // 设置滑动的监听，因为adpter实现了滑动回调的接口，所以这里直接设置adpter
        mViewPager.addOnPageChangeListener(mAdapter!!)
        // 自己百度
        mViewPager.offscreenPageLimit = 5

        // 设置触摸事件的分发
        setOnTouchListener { v, event -> // 传递给ViewPager 进行滑动处理
            mViewPager.dispatchTouchEvent(event)
        }
    }

    /**
     * 设置显示的数据，进行一层封装
     * @param lists
     */
    fun setViewList(lists: List<View?>?) {
        if (lists == null) {
            return
        }
        mViewList.clear()
        for (view in lists) {
//            val layout = FrameLayout(context)
            // 设置padding 值，默认缩小
//            layout.scaleX = 0.83f
//            layout.scaleY = 0.83f
//            layout.alpha = 0.5f
            //            layout.setTranslationX(mAdapter.dp2px(-60));
//            layout.addView(view)
            if (view != null) {
                mViewList.add(view)
            }
        }
        // 刷新数据
        mAdapter!!.notifyDataSetChanged()
        mViewList[0].bringToFront()
    }

    /**
     * 当将某一个作为最中央时的回调
     * @param listener
     */
    fun setOnPageSelectListener(listener: OnPageSelectListener?) {
        this.listener = listener
    }

    // 显示的回调
    override fun select(position: Int) {
        if (listener != null) {
            listener!!.select(position)
        }
    }
}