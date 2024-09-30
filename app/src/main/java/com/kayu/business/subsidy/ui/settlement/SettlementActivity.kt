package com.kayu.business.subsidy.ui.settlement

import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.flyco.tablayout.TabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.databinding.ActivitySettlementBinding
import com.kayu.utils.AppUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.status_bar_set.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettlementActivity : BaseActivity<ActivitySettlementBinding, SettlementViewModel>() {

    override val mViewModel: SettlementViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun ActivitySettlementBinding.initView() {

        mViewModel.title = intent.getStringExtra("title").toString()
        mViewModel.back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.settlementTitle.titleNameTv.text = mViewModel.title
        mBinding.settlementTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.settlementTitle.titleBackTv.text = mViewModel.back

        mViewModel.mTabEntities.add(TabEntity("收益明细", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mViewModel.mTabEntities.add(TabEntity("提现记录", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mBinding.settlementListCtl.setTabData(mViewModel.mTabEntities)
        mBinding.settlementListCtl.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mBinding.settlementListVp.currentItem = position
            }

            override fun onTabReselect(position: Int) {}
        })

        mBinding.settlementListVp.offscreenPageLimit = 3
        mBinding.settlementListVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                mBinding.settlementListCtl.currentTab = position

            }
        })
        mViewModel.mFragments.add(MySettlementFragment())
        mViewModel.mFragments.add(CashOutRecordFragment())
        val adapter = MyPagerAdapter(supportFragmentManager, mViewModel.mFragments,lifecycle)
        mBinding.settlementListVp.adapter = adapter
        mBinding.settlementListVp.currentItem = 0
        mBinding.settlementListCtl.currentTab = 0

    }

    override fun initObserve() {
        mViewModel.incomeDataResult.observe(this){it ->
            parseState(it,{
                mBinding.settleTodayIncome.text = AppUtil.getStringAmount(it.tdSumAmt)
                mBinding.settleMonthIncome.text = AppUtil.getStringAmount(it.tmSumAmt)
                mBinding.settleLastMonthIncome.text = AppUtil.getStringAmount(it.lmSumAmt)
            },{

            })
        }

    }

    override fun initRequestData() {
        mViewModel.getIncomeStatistics()
    }

}