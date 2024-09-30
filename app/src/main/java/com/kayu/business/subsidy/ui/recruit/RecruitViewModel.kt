package com.kayu.business.subsidy.ui.recruit

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.InviteInfoBean
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecruitViewModel  @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var inviteInfoDataResult = MutableLiveData<ResultState<InviteInfoBean?>>()

    /**
     *获取邀请码信息
     */
    fun getInviteInfo(){
        request({mRepository.getInviteInfo()},inviteInfoDataResult)
    }

}