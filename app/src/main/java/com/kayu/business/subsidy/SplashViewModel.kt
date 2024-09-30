package com.kayu.business.subsidy

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.UpgradeInfo

import com.kayu.business.subsidy.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val mRepository: LoginRepository) : BaseViewModel() {

    var upgradeInfoLiveData = MutableLiveData<ResultState<UpgradeInfo>>()

    /**
     * 检查app更新
     */
    fun checkUpgrade(version: String) {
        request({mRepository.checkUpgrade(version)},upgradeInfoLiveData)
    }
}