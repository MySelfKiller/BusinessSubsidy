package com.kayu.business.subsidy.ui

import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(private val mainRepository: MainRepository) : BaseViewModel() {

}