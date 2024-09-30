package com.kayu.business.subsidy.ui.mine

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.OldSystemParam
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
//    var weChatResult = MutableLiveData<ResultState<OldSystemParam>>()
//    var customerResult = MutableLiveData<ResultState<OldSystemParam>>()
//    var weChatLiveData = MutableLiveData<OldSystemParam>()
//    var customerLiveData = MutableLiveData<OldSystemParam>()


//    fun getWeChat(){
//        request({mRepository.getWeChat()},weChatResult)
//    }
//
//    fun getCustomer(){
//        request({mRepository.getCustomer()},customerResult)
//    }
}