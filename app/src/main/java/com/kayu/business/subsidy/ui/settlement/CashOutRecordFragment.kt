package com.kayu.business.subsidy.ui.settlement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.CashOutBean
import com.kayu.business.subsidy.databinding.FragmentCashOutRecordBinding
import com.kayu.business.subsidy.databinding.ItemCashOutRecordLayBinding
import com.kayu.business.subsidy.ui.WebViewActivity
import com.kayu.utils.AppUtil.getStringAmount
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CashOutRecordFragment : BaseFragment<FragmentCashOutRecordBinding, CashOutRecordViewModel>() {
    var mHasLoadedOnce = false // 页面已经加载过
//    var isCreated = false

    override val mViewModel: CashOutRecordViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun FragmentCashOutRecordBinding.initView() {

//        refreshLayout = view!!.findViewById<View>(R.id.refreshLayout) as RefreshLayout
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
//            //            mHasLoadedOnce = true;
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        isCreated = false
//    }

    override fun initObserve() {
        mViewModel.cashOutDetailRULResult.observe(this) { it ->
            parseState(it, {
                if (it?.url1.isNullOrEmpty()){
                    mViewModel.cashOutDetailRUL = it?.url1.toString()
                }
            }, {
            })
        }

        mViewModel.cashOutListResult.observe(this){ it ->
            parseState(it,{
                val listNewData = mutableListOf<CashOutBean>()
                listNewData.addAll(it.list)
                if (null == mViewModel.cashOutLiveData.value ) {
                    mViewModel.cashOutLiveData.value = mutableListOf()
                } else {
                    if (mViewModel.isRefresh) {
                        mViewModel.cashOutLiveData.value?.clear()
                    }
                }
                mViewModel.cashOutLiveData.value?.addAll(listNewData)
                loadCashOutListData(mViewModel.cashOutLiveData.value as MutableList<CashOutBean>)
            },{
                LogUtil.e(" incomeListResultData ",it.errorMsg)
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

    private fun loadCashOutListData(list:MutableList<CashOutBean>){
        if (null != mViewModel.adapter) {
            mViewModel.adapter?.data?.clear()
            mViewModel.adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<CashOutBean>){
        val newList = mutableListOf<CashOutBean>()
        newList.addAll(list)
        mViewModel.adapter = object : BaseQuickAdapter<CashOutBean, BaseDataBindingHolder<ItemCashOutRecordLayBinding>>
            (R.layout.item_cash_out_record_lay, newList) {
            override fun convert(
                holder: BaseDataBindingHolder<ItemCashOutRecordLayBinding>,
                item: CashOutBean
            ) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemRecordUsername.text = item.username
                    holder.dataBinding!!.itemRecordBankAccount.text = item.bankName+"（"+item.bankNo.substring(item.bankNo.length-4)+"）"
                    holder.dataBinding!!.itemRecordAmount.text =  "提现金额：" + getStringAmount(item.amount)+"元"
//                    holder.dataBinding!!.itemSettleArriveAmount.text = "到账金额：" + getStringAmount(item.actualAmount)
                    holder.dataBinding!!.itemRecordState.text = item.stateTitle
                    holder.dataBinding!!.itemRecordTime.text = item.createTime
                    if (item.state ==3){
                        holder.dataBinding!!.itemRecordBtnDetail.visibility = View.VISIBLE
                        holder.dataBinding!!.itemSettleArriveAmount.text = getStringAmount(item.actualAmount)
                        holder.dataBinding!!.settleArriveAmountLay.visibility = View.VISIBLE
                        holder.dataBinding!!.itemRecordBtnDetail.setOnClickListener(object :
                            NoMoreClickListener(){
                            override fun OnMoreClick(view: View) {
                                if (mViewModel.cashOutDetailRUL.isEmpty()){
                                    ToastUtils.show("获取中，稍后重试")
                                    mViewModel.getCashOutDetailRUL()
                                }else {
                                    val intent = Intent(context, WebViewActivity::class.java)
                                    val sb = StringBuilder()
                                    sb.append(mViewModel.cashOutDetailRUL)
                                    if (mViewModel.cashOutDetailRUL.contains("?")) {
                                        sb.append("&token=")
                                    } else {
                                        sb.append("?token=")
                                    }
                                    sb.append(SMApplication.instance.token)
                                    sb.append("&id="+item.id)
                                    intent.putExtra("url", sb.toString())
                                    intent.putExtra("from", "订单详情")
                                    startActivity(intent)
                                }
                            }
                            override fun OnMoreErrorClick() { }
                        })
                    }else {
                        holder.dataBinding!!.itemRecordBtnDetail.visibility = View.GONE
                        holder.dataBinding!!.settleArriveAmountLay.visibility = View.GONE
                    }
                }
            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(requireContext()).inflate(R.layout.empty_view_tab, null))
        mBinding.listRecycler.adapter = mViewModel.adapter
    }

    override fun initRequestData() {
        mViewModel.getCashOutDetailRUL()
    }

    private fun reqListData(page: Int){
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = page
        reqDateMap["pageSize"] = 10
        mViewModel.getCashOutList(reqDateMap)

    }
}