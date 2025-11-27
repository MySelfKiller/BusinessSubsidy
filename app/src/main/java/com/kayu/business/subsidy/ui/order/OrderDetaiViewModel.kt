package com.kayu.business.subsidy.ui.order

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.ItemOrderBean
import com.kayu.business.subsidy.data.bean.OrderDetailBean
import com.kayu.business.subsidy.data.bean.OrderStateBean
import com.kayu.business.subsidy.data.bean.SystemParamBean
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemOrderLayBinding
import com.kayu.business.subsidy.databinding.ItemStateOrderDetailBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderDetaiViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var orderID : Long = -1
    var isRefresh = false
    val orderDetailLiveData = MutableLiveData<ResultState<OrderDetailBean>>()
    var adapter: BaseQuickAdapter<OrderStateBean, BaseDataBindingHolder<ItemStateOrderDetailBinding>>? = null

    var customerResult = MutableLiveData<ResultState<SystemParamBean?>>()
    var customerLiveData = MutableLiveData<SystemParamBean?>()


    fun getWeChatCustomer(){
        request({ mRepository.getSysParam(4) }, customerResult)
    }

    fun getOrderDetail(id: Long){
        request({mRepository.getOrderDetail(id)},orderDetailLiveData)
    }
}