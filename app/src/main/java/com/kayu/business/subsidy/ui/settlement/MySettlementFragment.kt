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
import com.kayu.business.subsidy.databinding.FragmentMySettlementBinding
import com.kayu.business.subsidy.databinding.ItemMySettlementLayBinding
import com.kayu.utils.AppUtil.getStringAmount
import com.kayu.utils.LogUtil
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySettlementFragment : BaseFragment<FragmentMySettlementBinding, MySettlementViewModel>() {
    var mHasLoadedOnce = false // 页面已经加载过
//    var isCreated = false

    override val mViewModel: MySettlementViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun FragmentMySettlementBinding.initView() {
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

    override fun initObserve() {
        mViewModel.settlementListResult.observe(this){ it ->
            parseState(it,{
//                mBinding.mySettlementAmount.text = "我的今日结算：" + getStringAmount(it.sumMoney) + "元"
                val listNewData = mutableListOf<SettlementBean>()
                listNewData.addAll(it.list)
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
                LogUtil.e("mySettlementListResult---settlementListResult--",it.errorMsg)
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
        mViewModel.adapter = object : BaseQuickAdapter<SettlementBean, BaseDataBindingHolder<ItemMySettlementLayBinding>>
            (R.layout.item_my_settlement_lay, newList) {
            override fun convert(
                holder: BaseDataBindingHolder<ItemMySettlementLayBinding>,
                item: SettlementBean
            ) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemSettleProduct.text = item.productName+"（"+item.name+"）"
                    holder.dataBinding!!.itemSettleTime.text = item.createTime
                    holder.dataBinding!!.itemSettlePrice.text = getStringAmount(item.amount)

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