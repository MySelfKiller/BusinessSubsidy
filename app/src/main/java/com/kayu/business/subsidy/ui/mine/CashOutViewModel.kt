package com.kayu.business.subsidy.ui.mine

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.DebitCardBean
import com.kayu.business.subsidy.data.bean.SystemParamBean
import com.kayu.business.subsidy.data.bean.UserDetails
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CashOutViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var title = "标题"
    var back = "返回"
//    var rate:Double = 0.0
    var selectedCard: DebitCardBean? = null//已选择银行卡
    var tips1 = ""
    var cash_tips = ""
    var cardsList: MutableList<DebitCardBean>? = null
    var userDetails : UserDetails? = null

    var systemParamResult = MutableLiveData<ResultState<SystemParamBean>>()
    var cardListResult = MutableLiveData<ResultState<MutableList<DebitCardBean>>>()
    var userDetailsResult = MutableLiveData<ResultState<UserDetails>>()
    var applyCashOutResult = MutableLiveData<ResultState<Any?>>()
    var delDebitCardResult = MutableLiveData<ResultState<Any?>>()


    /**
     * 获取储蓄卡列表
     */
    fun getCardList() {
        request({ mRepository.getCardList() }, cardListResult)
    }

    fun getUserDetail() {
        request({ mRepository.getUserDetail() }, userDetailsResult)
    }

    /**
     * 请求系统参数,请求税率
     */
    fun getSysParam() {
        request({ mRepository.getSysParam(3) }, systemParamResult)
    }

    /**
     * 申请提现
     */
    fun applyCashOut(reqDateMap: HashMap<String,Any>){
        request({mRepository.applyCashOut(reqDateMap)},applyCashOutResult,true,"确认中...")
    }

    /**
     * 解绑储蓄卡
     */
    fun delDebitCard(reqDateMap: HashMap<String,Any>){
        request({mRepository.delDebitCard(reqDateMap)},delDebitCardResult,true,"稍等...")
    }

}