package com.kayu.business.subsidy.ui.home

import com.hoom.library.base.mvvm.BaseViewModel
import com.kayu.business.subsidy.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(private val mRepository: MainRepository) :  BaseViewModel() {
}