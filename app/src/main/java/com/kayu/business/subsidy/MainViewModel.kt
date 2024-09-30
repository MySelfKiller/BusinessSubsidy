package com.kayu.business.subsidy

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.ActivityList
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var activityListResult = MutableLiveData<ResultState<ActivityList>>()
    var msgUnReadResult = MutableLiveData<ResultState<Any?>>()

    /**
     * 获取储蓄卡列表
     */
    fun getActivityList() {
        request({ mRepository.getActivityList() }, activityListResult)
    }

    fun getMsgUnreadNum(){
        request({mRepository.getMsgUnreadNum()},msgUnReadResult)
    }
}