package com.kayu.business.subsidy.ui.team

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.TeamStatDataBean
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class TeamViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var title = "标题"
    var back = "返回"
    var isRefresh = false

    var teamInfoLiveData = MutableLiveData<ResultState<TeamStatDataBean>>()
    var addWeChatResult = MutableLiveData<ResultState<Any?>>()

    fun getTeamInfo(){
        request({mRepository.getTeamInfo()}, teamInfoLiveData)
    }

    /**
     * 添加微信号
     */
    fun addWeCaht(weChat:String){
        request({mRepository.addWeCaht(weChat)},addWeChatResult)
    }
}