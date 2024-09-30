package com.kayu.business.subsidy.ui.settlement

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.SettlementBean
import com.kayu.business.subsidy.databinding.FragmentTeamSettlementBinding
import com.kayu.business.subsidy.databinding.ItemTeamSettlementLayBinding
import com.kayu.utils.AppUtil.getStringAmount
import com.kayu.utils.LogUtil
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamSettlementFragment : BaseFragment<FragmentTeamSettlementBinding, TeamSettlementViewModel>() {
    var mHasLoadedOnce = false // 页面已经加载过
//    var isCreated = false
    override val mViewModel: TeamSettlementViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun FragmentTeamSettlementBinding.initView() {
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
            reqListData(mViewModel.pageIndex)
        })
        mBinding.refreshLayout.setOnLoadMoreListener(OnLoadMoreListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnLoadMoreListener
            mViewModel.isLoadMore = true
            mViewModel.pageIndex += 1
            reqListData(mViewModel.pageIndex)
        })
        mBinding.listRecycler.layoutManager = LinearLayoutManager(requireContext())
//        isCreated = true
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !mHasLoadedOnce) {
//            mBinding.refreshLayout.autoRefresh()
            WaitDialog.show(
                requireActivity() as AppCompatActivity,
                "加载中..."
            )
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter!!.notifyDataSetChanged()
            }
            reqListData(mViewModel.pageIndex)
            mHasLoadedOnce = true
        }
    }
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        if (userVisibleHint && !mHasLoadedOnce) {
//            mBinding.refreshLayout.autoRefresh()
//            mHasLoadedOnce = true
//        }
//    }
//
//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser && !mHasLoadedOnce && isCreated) {
//            mBinding.refreshLayout.autoRefresh()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        isCreated = false
//    }

    override fun initObserve() {
        mViewModel.settlementListResult.observe(this){ it ->
            parseState(it,{item ->
//                mBinding.teamSettlementAmount.text = "团队今日结算：" + AppUtil.getStringAmount(it.sumMoney) + "元"
                val listNewData = mutableListOf<SettlementBean>()
                listNewData.addAll(item.list)
                if (null == mViewModel.settlementListData.value ) {
                    mViewModel.settlementListData.value = mutableListOf()
                } else {
                    if (mViewModel.isRefresh) {
                        mViewModel.settlementListData.value?.clear()
                    }
                }
                mViewModel.settlementListData.value?.addAll(listNewData)
                loadListData(mViewModel.settlementListData.value as MutableList<SettlementBean>)
            },{
                LogUtil.e("mySettlementListResult",it.errorMsg)
            })
            if (mViewModel.isRefresh) {
                mBinding.refreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            if (mViewModel.isLoadMore) {
                mBinding.refreshLayout.finishLoadMore()
                mViewModel.isLoadMore = false
            }
            WaitDialog.dismiss()
        }
    }

    private fun loadListData(list:MutableList<SettlementBean>){
        if (null != mViewModel.adapter) {
            mViewModel.adapter?.data?.clear()
            mViewModel.adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<SettlementBean>){
        val newList = mutableListOf<SettlementBean>()
        newList.addAll(list)
        mViewModel.adapter = object : BaseQuickAdapter<SettlementBean, BaseDataBindingHolder<ItemTeamSettlementLayBinding>>
            (R.layout.item_team_settlement_lay, newList) {
            override fun convert(
                holder: BaseDataBindingHolder<ItemTeamSettlementLayBinding>,
                item: SettlementBean
            ) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemMySettlementShow1.text = "类型：" + item.productName
                    holder.dataBinding!!.itemMySettlementShow2.text = "来源：" + item.content


                    holder.dataBinding!!.itemMySettlementShow3.text = "金额：" + getStringAmount(item.amount)
                    holder.dataBinding!!.itemMySettlementShow4.text = "日期：" + item.content
                    holder.dataBinding!!.itemMySettlementParentName.text = "客户经理：" + item.name

                }
            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(requireContext()).inflate(R.layout.empty_view_tab, null))
        mBinding.listRecycler.adapter = mViewModel.adapter
    }

    override fun initRequestData() {
    }

    private fun reqListData(page: Int){
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = page
        reqDateMap["pageSize"] = 10
        mViewModel.getSettlementList(reqDateMap)

    }

}