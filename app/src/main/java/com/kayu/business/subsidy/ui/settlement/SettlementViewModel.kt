package com.kayu.business.subsidy.ui.settlement

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.flyco.tablayout.listener.CustomTabEntity
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.IncomeStaBean
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettlementViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var title = "标题"
    var back = "返回"

    val mTabEntities = ArrayList<CustomTabEntity>()
    val mFragments = ArrayList<Fragment>()

    var incomeDataResult = MutableLiveData<ResultState<IncomeStaBean>>()

    /**
     * 获取收益数据统计
     */
    fun getIncomeStatistics(){
        request({mRepository.getIncomeStatistics()},incomeDataResult)
    }

}