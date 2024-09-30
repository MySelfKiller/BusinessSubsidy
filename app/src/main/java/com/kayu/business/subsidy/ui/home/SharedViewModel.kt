package com.kayu.business.subsidy.ui.home

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.ext.requestNoCheck
import com.hoom.library.common.network.ApiResponse
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.InviteInfoBean
import com.kayu.business.subsidy.data.bean.UserDetails
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val mRepository: MainRepository) :  BaseViewModel() {
//    var inviteInfoDataResult = MutableLiveData<ResultState<InviteInfoBean?>>()
    var userDetailsResult = MutableLiveData<ResultState<UserDetails>>()

    /**
     *获取邀请码信息
     */
//    fun getInviteInfo(){
//        request({mRepository.getInviteInfo()},inviteInfoDataResult)
//    }

    fun getUserDetail() {
        request({ mRepository.getUserDetail() }, userDetailsResult,true)
    }
}