package com.kayu.business.subsidy.ui.recruit

import com.hoom.library.base.mvvm.BaseViewModel
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecruitTeamViewModel @Inject constructor(private val mRepository: MainRepository) :  BaseViewModel() {

}