package com.kayu.business.subsidy.ui.login

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.ImageCode
import com.kayu.business.subsidy.data.bean.LoginDataBean

import com.kayu.business.subsidy.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : BaseViewModel() {
//    var userAgreementData = MutableLiveData<ResultState<SystemParam>>()

//    private val _loginForm = MutableLiveData<LoginFormState>()
//    val loginFormState: LiveData<LoginFormState> = _loginForm

    var loginResult = MutableLiveData<ResultState<LoginDataBean>>()

    var sendSmsLiveData = MutableLiveData<ResultState<Any?>>()
    var imageCodeLivedata = MutableLiveData<ResultState<ImageCode>>()

    /**
     * 发送短信验证码
     */
    fun sendSMS(phone: String) {
        request({ loginRepository.sendSmsCode(phone) },sendSmsLiveData,true,"发送验证码...")
    }

    /**
     * 获取图片验证码
     */
    fun getImageCode(){
        request({ loginRepository.getImageCode() },imageCodeLivedata)
    }

    fun login(reqDateMap: HashMap<String,Any>) {
        request({loginRepository.login(reqDateMap)},loginResult,true,"确认中...")
    }

}