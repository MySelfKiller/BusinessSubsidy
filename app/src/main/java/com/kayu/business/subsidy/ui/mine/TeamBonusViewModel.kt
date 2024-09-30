package com.kayu.business.subsidy.ui.mine

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.*
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeamBonusViewModel @Inject constructor(private val mRepository: MainRepository) :  BaseViewModel() {

    var teamBonusListResult = MutableLiveData<ResultState<TeamBonusList>>()
    var teamBonusListLiveData = MutableLiveData<MutableList<TeamUserBonus>>()



    /**
     * 获取团队分红用户列表
     */
    fun getTeamBonusList(reqDateMap: HashMap<String,Any>){
        request({mRepository.getTeamSubsidyList(reqDateMap)},teamBonusListResult)
    }
}