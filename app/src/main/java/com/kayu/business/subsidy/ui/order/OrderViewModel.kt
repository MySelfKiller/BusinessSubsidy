package com.kayu.business.subsidy.ui.order

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.*
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemOrderLayBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var searchKey = ""
    var isLoadMore = false
    var isRefresh = false
    var pageIndex = 0
    var adapter: BaseQuickAdapter<ItemOrderBean, BaseDataBindingHolder<ItemOrderLayBinding>>? = null
    var state: Int = 0          //订单类型
    var mHasLoadedOnce = false // 页面已经加载过

    var orderListResult = MutableLiveData<ResultState<OrderPageData>>()
    var deleteOrderResult = MutableLiveData<ResultState<Any?>>()
    var orderListData = MutableLiveData<MutableList<ItemOrderBean>>()


    /**
     * 查询订单列表
     */
    fun getOrderList(reqDateMap: HashMap<String,Any>){
        request({mRepository.getOrderList(reqDateMap)},orderListResult)
    }
    /**
     * 删除订单
     */
    fun deleteOrder(id: Long){
        request({mRepository.deleteOrder(id)},deleteOrderResult,true,"确认中...")
    }

}