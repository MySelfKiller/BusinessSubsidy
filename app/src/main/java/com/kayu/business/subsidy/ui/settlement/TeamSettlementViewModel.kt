package com.kayu.business.subsidy.ui.settlement

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.SettlementBean
import com.kayu.business.subsidy.data.bean.SettlementListBean
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemTeamSettlementLayBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeamSettlementViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var isLoadMore = false
    var isRefresh = false
    var pageIndex = 0
    var adapter: BaseQuickAdapter<SettlementBean, BaseDataBindingHolder<ItemTeamSettlementLayBinding>>? = null

    var settlementListResult = MutableLiveData<ResultState<SettlementListBean>>()
    var settlementListData = MutableLiveData<MutableList<SettlementBean>>()

    fun getSettlementList(reqDateMap: HashMap<String,Any>){
        request({mRepository.getSettlementList(reqDateMap)},settlementListResult)
    }
}