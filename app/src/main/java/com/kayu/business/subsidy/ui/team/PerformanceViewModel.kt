package com.kayu.business.subsidy.ui.team

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.StatisticData
import com.kayu.business.subsidy.data.bean.SystemParamBean
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel()  {
    var isRefresh = false
    var mHasLoadedOnce = false // 页面已经加载过

    var subsidyRuleResult = MutableLiveData<ResultState<SystemParamBean>>()
    var statisticLiveData = MutableLiveData<ResultState<StatisticData>>()


    /**
     * 补贴政策
     */
    fun getSubsidyRule(){
        request({ mRepository.getSysParam(6) }, subsidyRuleResult)
    }

    /**
     * 业绩统计数据
     */
    fun getStatisticsData(){
        request({mRepository.getStatistics()},statisticLiveData)
    }
}