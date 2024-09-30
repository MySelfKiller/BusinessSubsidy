package com.kayu.business.subsidy.ui.team

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.TeamBonusList
import com.kayu.business.subsidy.data.bean.TeamUserBonus
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemTeamSubsidyBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class TeamSubsidyViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {

    var isRefresh = false
    var isLoadMore = false
    var pageIndex = 0
    var title = "标题"
    var back = "返回"

    var adapter: BaseQuickAdapter<TeamUserBonus, BaseDataBindingHolder<ItemTeamSubsidyBinding>>? = null

    var teamSubsidyListResult = MutableLiveData<ResultState<TeamBonusList>>()
    var teamSubsidyListLiveData = MutableLiveData<MutableList<TeamUserBonus>>()



    /**
     * 获取团队分红用户列表
     */
    fun getTeamBonusList(reqDateMap: HashMap<String,Any>){
        request({mRepository.getTeamSubsidyList(reqDateMap)},teamSubsidyListResult)
    }
}