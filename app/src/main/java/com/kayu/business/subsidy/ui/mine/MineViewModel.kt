package com.kayu.business.subsidy.ui.mine

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.*
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemNavOptionBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var rankDetailResult = MutableLiveData<ResultState<SystemParamBean>>()
    var rankDetailLiveData = MutableLiveData<SystemParamBean>()
    var userDetailsResult = MutableLiveData<ResultState<UserDetails>>()
    var navListLiveData = MutableLiveData<ResultState<MutableList<NavOptionBean>>>()
    var adapter: BaseQuickAdapter<NavOptionBean, BaseDataBindingHolder<ItemNavOptionBinding>>? = null
    //提现功能页URL返回数据
    var cashOutDetailRULResult = MutableLiveData<ResultState<SystemParamBean>>()
    //提现功能页URL
    var cashOutDetailRUL = ""


    fun getRankDetail(){
        request({ mRepository.getSysParam(5) }, rankDetailResult)
    }
     fun getUserDetail(){
         request({mRepository.getUserDetail()},userDetailsResult)
     }

    /**
     * 请求系统参数,请求用户等级说明
     */
    fun getNavList() {
        request({ mRepository.getNavList() }, navListLiveData)
    }

    /**
     * 获取提现功能页URL链接
     */
    fun getCashOutRUL(){
        request({ mRepository.getSysParam(11) }, cashOutDetailRULResult)
    }

}