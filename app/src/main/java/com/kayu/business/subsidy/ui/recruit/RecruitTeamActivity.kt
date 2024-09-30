package com.kayu.business.subsidy.ui.recruit

import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.databinding.ActivityRecruitTeamBinding
import com.kayu.business.subsidy.ui.settlement.MyPagerAdapter
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.status_bar_set.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecruitTeamActivity : BaseActivity<ActivityRecruitTeamBinding, RecruitTeamViewModel>() {
    private var title = "标题"
    private var back = "返回"

    override val mViewModel: RecruitTeamViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun ActivityRecruitTeamBinding.initView() {
        title = intent.getStringExtra("title").toString()
        back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.recruitTitle.titleNameTv.text = title
        mBinding.recruitTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.recruitTitle.titleBackTv.text = back

//        val navController = findNavController(R.id.host_fragment_main)

//        mBinding.recruitTeamLay.setOnClickListener(object : NoMoreClickListener(){
//            override fun OnMoreClick(view: View) {
//                LogUtil.e("团队招募---点击事件",mBinding.recruitTeamLay.isClickable.toString())
//                if (mBinding.recruitTeamLay.isClickable){
//                    navController.navigate(R.id.navigation_recruit)
//                    mBinding.recruitExtendLay.isClickable = true
//                    mBinding.recruitTeamLay.isClickable = false
//                    ConstraintSet().also { adx->
//                        adx.clone(mBinding.recruitActivityLay)
//                        adx.connect(mBinding.recruitIndicator.id, ConstraintSet.START,mBinding.recruitTeamLay.id,ConstraintSet.START)
//                        adx.connect(mBinding.recruitIndicator.id, ConstraintSet.END,mBinding.recruitTeamLay.id,ConstraintSet.END)
//                    }.applyTo(mBinding.recruitActivityLay)
//                }
//            }
//
//            override fun OnMoreErrorClick() {
//
//            }
//
//        })
//        mBinding.recruitExtendLay.setOnClickListener(object : NoMoreClickListener(){
//            override fun OnMoreClick(view: View) {
//                LogUtil.e("推广文案---点击事件",mBinding.recruitExtendLay.isClickable.toString())
//                if (mBinding.recruitExtendLay.isClickable){
//                    navController.navigate(R.id.navigation_promote)
//                    mBinding.recruitExtendLay.isClickable = false
//                    mBinding.recruitTeamLay.isClickable = true
//                    ConstraintSet().also { adx->
//                        adx.clone(mBinding.recruitActivityLay)
//                        adx.connect(mBinding.recruitIndicator.id, ConstraintSet.START,mBinding.recruitExtendLay.id,ConstraintSet.START)
//                        adx.connect(mBinding.recruitIndicator.id, ConstraintSet.END,mBinding.recruitExtendLay.id,ConstraintSet.END)
//                    }.applyTo(mBinding.recruitActivityLay)
//
//                }
//            }
//
//            override fun OnMoreErrorClick() {
//            }
//
//        })
//        if (title.contains("朋友圈")){
//            mBinding.recruitExtendLay.performClick()
//        }

    }

    override fun initObserve() {

    }

    override fun initRequestData() {

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}