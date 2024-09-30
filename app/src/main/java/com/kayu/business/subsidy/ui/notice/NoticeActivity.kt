package com.kayu.business.subsidy.ui.notice

import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.databinding.ActivityNoticeBinding
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.status_bar_set.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : BaseActivity<ActivityNoticeBinding,NoticeViewModel>() {
    override val mViewModel: NoticeViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun initObserve() {
    }

    override fun initRequestData() {
    }

    override fun ActivityNoticeBinding.initView() {
        mBinding.noticeTitle.titleNameTv.text = "消息通知"
        mBinding.noticeTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        supportFragmentManager.beginTransaction()
            .replace(mBinding.container.id, NoticeFragment.newInstance())
            .commitNow()
    }
}