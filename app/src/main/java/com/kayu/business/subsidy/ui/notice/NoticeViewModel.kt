package com.kayu.business.subsidy.ui.notice

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.flyco.tablayout.TabEntity
import com.flyco.tablayout.listener.CustomTabEntity
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.MessageBean
import com.kayu.business.subsidy.data.bean.MessageList
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemMessageLayBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var isLoadMore = false
    var isRefresh = false
    var pageIndex = 0
    val mTabEntities = ArrayList<CustomTabEntity>()
    var msgType :Int?= null
    var mHasLoadedOnce = false // 页面已经加载过
    var messageListResult = MutableLiveData<ResultState<MessageList>>()
    var messageListData = MutableLiveData<MutableList<MessageBean>>()
    var adapter: BaseQuickAdapter<MessageBean, BaseDataBindingHolder<ItemMessageLayBinding>>? = null

    init {
        mTabEntities.add(TabEntity("全部", R.mipmap.ic_msg_select, R.mipmap.ic_msg_default))
        mTabEntities.add(TabEntity("系统消息", R.mipmap.ic_msg_select, R.mipmap.ic_msg_default))
        mTabEntities.add(TabEntity("平台公告", R.mipmap.ic_msg_select, R.mipmap.ic_msg_default))
    }

    fun getNoticeList(reqDateMap: HashMap<String,Any?>){
        request({mRepository.getMessageList(reqDateMap)},messageListResult)
    }
}