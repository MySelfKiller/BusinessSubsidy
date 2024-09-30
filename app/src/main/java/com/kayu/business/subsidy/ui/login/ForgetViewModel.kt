package com.kayu.business.subsidy.ui.login

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState

import com.kayu.business.subsidy.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgetViewModel @Inject constructor(private val mRepository: LoginRepository) : BaseViewModel() {
    var sendSmsLiveData = MutableLiveData<ResultState<Any?>>()
    var setPwdLiveData = MutableLiveData<ResultState<Any?>>()

    fun sendSetPwdSmsCode(phone: String) {
        request({ mRepository.sendSetPwdSmsCode(phone) },sendSmsLiveData,true,"发送验证码...")
    }

    fun setPassword(reqDateMap: HashMap<String,Any>) {
        request({mRepository.setPassword(reqDateMap)},setPwdLiveData,true)
    }
    fun setForgetPassword(reqDateMap: HashMap<String,Any>) {
        request({mRepository.setForgetPassword(reqDateMap)},setPwdLiveData,true)
    }
}