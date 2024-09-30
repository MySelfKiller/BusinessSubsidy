package com.kayu.business.subsidy.ui.mine

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RealNameViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var title = "标题"
    var back = "返回"
    var username: String? = null
    var idNo: String? = null

    var userVerifiedResult = MutableLiveData<ResultState<Any?>>()

    /**
     * 实名认证
     */
    fun setUserVerified(reqDateMap: HashMap<String,Any>){
        request({mRepository.setUserVerified(reqDateMap)},userVerifiedResult,true,"确认中...")
    }
    /**
     * 添加储蓄卡
     */
    fun addDebitCard(reqDateMap: HashMap<String,Any>){
        request({mRepository.addDebitCard(reqDateMap)},userVerifiedResult,true,"确认中...")
    }

}