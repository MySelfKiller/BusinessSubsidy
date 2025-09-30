package com.kayu.business.subsidy.ui.settlement

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.CashOutBean
import com.kayu.business.subsidy.data.bean.CashOutListBean
import com.kayu.business.subsidy.data.bean.SystemParamBean
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemCashOutRecordLayBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CashOutRecordViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var isLoadMore = false
    var isRefresh = false
    var pageIndex = 0
    var adapter: BaseQuickAdapter<CashOutBean, BaseDataBindingHolder<ItemCashOutRecordLayBinding>>? = null

    var cashOutListResult = MutableLiveData<ResultState<CashOutListBean>>()
    var cashOutLiveData = MutableLiveData<MutableList<CashOutBean>>()

    var cashOutDetailRUL = ""//提现记录详情页URL链接
    var cashOutDetailRULResult = MutableLiveData<ResultState<SystemParamBean>>()//提现记录详情页URL链接结果


    fun getCashOutList(reqDateMap: HashMap<String,Any>){
        request({mRepository.getCashOutList(reqDateMap)},cashOutListResult)
    }

    /**
     * 获取提现成功详情页URL链接
     */
    fun getCashOutDetailRUL(){
        request({ mRepository.getSysParam(11) }, cashOutDetailRULResult)
    }

}