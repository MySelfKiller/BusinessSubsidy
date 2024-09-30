package com.kayu.business.subsidy.ui.team

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.TeamUserBonus
import com.kayu.business.subsidy.databinding.ActivityTeamSubsidyBinding
import com.kayu.business.subsidy.databinding.ItemTeamBonusBinding
import com.kayu.business.subsidy.databinding.ItemTeamSubsidyBinding
import com.kayu.utils.AppUtil
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamSubsidyActivity : BaseActivity<ActivityTeamSubsidyBinding, TeamSubsidyViewModel>() {



    override val mViewModel: TeamSubsidyViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun initObserve() {
        mViewModel.teamSubsidyListResult.observe(this){ it->
            parseState(it,{
                val listNewData = mutableListOf<TeamUserBonus>()
                it.list?.let { it1 -> listNewData.addAll(it1) }
                if (null == mViewModel.teamSubsidyListLiveData.value ) {
                    mViewModel.teamSubsidyListLiveData.value = mutableListOf()
                } else {
                    if (mViewModel.isRefresh) {
                        mViewModel.teamSubsidyListLiveData.value?.clear()
                    }
                }
                mViewModel.teamSubsidyListLiveData.value?.addAll(listNewData)
                loadListData(mViewModel.teamSubsidyListLiveData.value as MutableList<TeamUserBonus>)
            },{
                LogUtil.e("TeamSubsidyActivity---teamSubsidyListResult---",it.errorMsg+it.errorLog)
                ToastUtils.show("获取数据失败，稍后重试!")
            })
            if (mViewModel.isRefresh) {
                mBinding.refreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            if (mViewModel.isLoadMore) {
                mBinding.refreshLayout.finishLoadMore()
                mViewModel.isLoadMore = false
            }
        }
    }

    override fun initRequestData() {
    }

    override fun ActivityTeamSubsidyBinding.initView() {
        mViewModel.title = intent.getStringExtra("title").toString()
        mViewModel.back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.teamSubsidyTitle.titleNameTv.text = mViewModel.title
        mBinding.teamSubsidyTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.teamSubsidyTitle.titleBackTv.text = mViewModel.back

        mBinding.refreshLayout.setEnableAutoLoadMore(false)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能

        mBinding.refreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹

        mBinding.refreshLayout.setEnableOverScrollDrag(true)
        mBinding.refreshLayout.setOnRefreshListener(OnRefreshListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnRefreshListener
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter?.notifyDataSetChanged()
            }
            reqTeamList()

        })
        mBinding.refreshLayout.setOnLoadMoreListener(OnLoadMoreListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnLoadMoreListener
            mViewModel.isLoadMore = true
            mViewModel.pageIndex += 1
            reqTeamList()
        })
        mBinding.teamSubsidyRecycleView.layoutManager = LinearLayoutManager(this@TeamSubsidyActivity)
//        mBinding.refreshLayout.autoRefresh()
        WaitDialog.show(this@TeamSubsidyActivity,
            "加载中..."
        )
        mViewModel.isRefresh = true
        mViewModel.pageIndex = 1
        if (null != mViewModel.adapter) {
            mViewModel.adapter!!.data.clear()
            mViewModel.adapter?.notifyDataSetChanged()
        }
        reqTeamList()
    }

    private fun reqTeamList(){
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = mViewModel.pageIndex
        reqDateMap["pageSize"] = 10
        mViewModel.getTeamBonusList(reqDateMap)
    }

    private fun loadListData(list:MutableList<TeamUserBonus>){
        if (null != mViewModel.adapter) {
            mViewModel.adapter?.data?.clear()
            mViewModel.adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<TeamUserBonus>){
        val newList = mutableListOf<TeamUserBonus>()
        newList.addAll(list)
        mViewModel.adapter = object :
            BaseQuickAdapter<TeamUserBonus, BaseDataBindingHolder<ItemTeamSubsidyBinding>>(
                R.layout.item_team_subsidy, newList
            ) {

            @SuppressLint("SetTextI18n")
            override fun convert(holder: BaseDataBindingHolder<ItemTeamSubsidyBinding>, item: TeamUserBonus) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemTeamSubsidyDate.text = item.statDate
                    holder.dataBinding!!.itemTeamSubsidyAmount.text = "获得团队补贴："+AppUtil.getStringAmount(item.commission)+"元"
                    holder.dataBinding!!.itemTeamSubsidyLevel.text = item.rankTitle
                    holder.dataBinding!!.itemTeamSubsidyTotalSub.text = item.teamSettleCount.toString()
                    val income = AppUtil.getStringAmount(item.teamSettleAmount)
                    holder.dataBinding!!.itemTeamSubsidyTotalPer.text = income
                }

            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(this@TeamSubsidyActivity).inflate(R.layout.empty_view_tab, null))
        mBinding.teamSubsidyRecycleView.adapter = mViewModel.adapter
    }

}