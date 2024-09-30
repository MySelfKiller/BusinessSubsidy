package com.kayu.business.subsidy.ui.mine

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.TeamUserBonus
import com.kayu.business.subsidy.databinding.ActivityTeamBonusBinding
import com.kayu.business.subsidy.databinding.ItemTeamBonusBinding
import com.kayu.utils.NoMoreClickListener
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamBonusActivity : BaseActivity<ActivityTeamBonusBinding,TeamBonusViewModel>() {
    private var isRefresh = false
    private var isLoadMore = false
    private var pageIndex = 0
    private var title = "标题"
    private var back = "返回"
    private var adapter: BaseQuickAdapter<TeamUserBonus, BaseDataBindingHolder<ItemTeamBonusBinding>>? = null

    override val mViewModel: TeamBonusViewModel by viewModels()

    override fun ActivityTeamBonusBinding.initView() {
        title = intent.getStringExtra("title").toString()
        back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.bonusTitle.titleNameTv.text = title
        mBinding.bonusTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.bonusTitle.titleBackTv.text = back

        mBinding.refreshLayout.setEnableAutoLoadMore(false)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能

        mBinding.refreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹

        mBinding.refreshLayout.setEnableOverScrollDrag(true)
        mBinding.refreshLayout.setOnRefreshListener(OnRefreshListener {
            if (isRefresh || isLoadMore) return@OnRefreshListener
            isRefresh = true
            pageIndex = 1
            if (null != adapter) {
                adapter!!.data.clear()
            }
//            mViewModel.getTeamBonusData()
            reqTeamList()

        })
        mBinding.refreshLayout.setOnLoadMoreListener(OnLoadMoreListener {
            if (isRefresh || isLoadMore) return@OnLoadMoreListener
            isLoadMore = true
            pageIndex += 1
            reqTeamList()
        })
        mBinding.bonusListRecycler.layoutManager = LinearLayoutManager(this@TeamBonusActivity)
//        mBinding.refreshLayout.autoRefresh()
        WaitDialog.show(this@TeamBonusActivity,
            "加载中..."
        )
        isRefresh = true
        pageIndex = 1
        if (null != adapter) {
            adapter!!.data.clear()
        }
    }

    private fun reqTeamList(){
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = pageIndex
        reqDateMap["pageSize"] = 10
        mViewModel.getTeamBonusList(reqDateMap)
    }

    override fun initObserve() {
//        mViewModel.teamStatDataResult.observe(this){ it->
//            parseState(it,{
//                mBinding.bonusInformShow1.text = AppUtil.getStringAmount(it.tdBs)
//                mBinding.bonusInformShow2.text = AppUtil.getStringAmount(it.ydBs)
//                mBinding.bonusInformShow3.text = AppUtil.getStringAmount(it.tmBs)
//                mBinding.bonusInformShow4.text = AppUtil.getStringAmount(it.totalBs)
//            },{
//                ToastUtils.show(it)
//            })
//        }
        mViewModel.teamBonusListResult.observe(this){ it->
            parseState(it,{
                val listNewData = mutableListOf<TeamUserBonus>()
                it.list?.let { it1 -> listNewData.addAll(it1) }
                if (null == mViewModel.teamBonusListLiveData.value ) {
                    mViewModel.teamBonusListLiveData.value = mutableListOf()
                } else {
                    if (isRefresh) {
                        mViewModel.teamBonusListLiveData.value?.clear()
                    }
                }
                mViewModel.teamBonusListLiveData.value?.addAll(listNewData)
                loadListData(mViewModel.teamBonusListLiveData.value as MutableList<TeamUserBonus>)
            },{
                ToastUtils.show(it)
            })
            if (isRefresh) {
                mBinding.refreshLayout.finishRefresh()
                isRefresh = false
            }
            if (isLoadMore) {
                mBinding.refreshLayout.finishLoadMore()
                isLoadMore = false
            }
            WaitDialog.dismiss()
        }
    }

    private fun loadListData(list:MutableList<TeamUserBonus>){
        if (null != adapter) {
            adapter?.data?.clear()
            adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<TeamUserBonus>){
        val newList = mutableListOf<TeamUserBonus>()
        newList.addAll(list)
        adapter = object :
            BaseQuickAdapter<TeamUserBonus, BaseDataBindingHolder<ItemTeamBonusBinding>>(
                R.layout.item_team_bonus, newList
            ) {

            @SuppressLint("SetTextI18n")
            override fun convert(holder: BaseDataBindingHolder<ItemTeamBonusBinding>, item: TeamUserBonus) {
                if (null != holder.dataBinding) {
//                    holder.dataBinding!!.itemBonusUserName.text = "团队成员：${item.username}"
//                    holder.dataBinding!!.itemBonusTime.text = item.createTime
//                    holder.dataBinding!!.itemBonusProductLogo.load(item.productLogo)
//                    holder.dataBinding!!.itemBonusProductName.text = item.productName
//                    val income = AppUtil.getStringAmount(item.money)
//                    holder.dataBinding!!.itemBonusAmount.text = income
                }

            }
        }
        adapter?.setEmptyView(LayoutInflater.from(this@TeamBonusActivity).inflate(R.layout.empty_view_tab, null))
        mBinding.bonusListRecycler.adapter = adapter
    }

    override fun initRequestData() {
    }
}