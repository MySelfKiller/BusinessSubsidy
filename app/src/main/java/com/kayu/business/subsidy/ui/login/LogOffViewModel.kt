package com.kayu.business.subsidy.ui.login

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState

import com.kayu.business.subsidy.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogOffViewModel @Inject constructor(private val loginRepository: LoginRepository) : BaseViewModel() {

    var logoffResult = MutableLiveData<ResultState<Any?>>()
    var sendSmsLiveData = MutableLiveData<ResultState<Any?>>()

    fun sendLogoffSmsCode() {
        request({ loginRepository.sendLogoffSmsCode() },sendSmsLiveData,true,"发送验证码...")
    }

    fun logoff(code:String) {
//        val sb = StringBuilder()
//        sb.append("api/v1/user/logoff?code=").append(code)
        request({ loginRepository.logoff(code) },logoffResult,true)
    }
}