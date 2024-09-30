package com.kayu.business.subsidy.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.flyco.tablayout.TabEntity
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.ItemOrderBean
import com.kayu.business.subsidy.databinding.ActivityOrderBinding
import com.kayu.business.subsidy.databinding.ItemOrderLayBinding
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kayu.utils.StringUtil.isEmpty
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class OrderActivity : BaseActivity<ActivityOrderBinding, OrderActivityViewModel>() {
    var title = "标题"
    var back = "返回"



    override val mViewModel: OrderActivityViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun ActivityOrderBinding.initView() {
        title = intent.getStringExtra("title").toString()
        back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.orderTitle.titleNameTv.text = title
        mBinding.orderTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.orderTitle.titleBackTv.text = back

        supportFragmentManager.beginTransaction()
            .replace(mBinding.container.id,OrderFragment.newInstance())
            .commitNow()
    }

    override fun initObserve() {

    }

    override fun initRequestData() {

    }

}