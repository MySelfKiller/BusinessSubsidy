package com.kayu.business.subsidy.ui.team

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.*
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemTeamDetailLayBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class TeamDetailViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {

    var isRefresh = false
    var isLoadMore = false
    var pageIndex = 0
    var title = "标题"
    var back = "返回"
    var searchKey = ""
    var adapter: BaseQuickAdapter<TeamUserBean, BaseDataBindingHolder<ItemTeamDetailLayBinding>>? = null

    var teamUserListResult = MutableLiveData<ResultState<TeamList>>()
    var teamUserListLiveData = MutableLiveData<MutableList<TeamUserBean>>()
//    var teamAchiDataResult = MutableLiveData<ResultState<TeamAchiDataBean>>()

    fun getTeamUserList(reqDateMap: HashMap<String,Any>){
        request({mRepository.getTeamList(reqDateMap)},teamUserListResult)
    }

    /**
     * 获取团队用户的团队业绩
     */
    fun getTeamAchv(subUserId: Long,teamAchiDataResult :MutableLiveData<ResultState<TeamAchiDataBean>>){
        request({mRepository.getTeamAchv(subUserId)},teamAchiDataResult,true)
    }

}