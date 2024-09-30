package com.kayu.business.subsidy.ui.recruit

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.flyco.tablayout.listener.CustomTabEntity
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.ExtendCopyBean
import com.kayu.business.subsidy.data.bean.ExtendCopyPage
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemExtensionLayoutBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PromoteViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var isLoadMore = false
    var isRefresh = false
    var pageIndex = 0
    val mTabEntities = ArrayList<CustomTabEntity>()

    var adapter: BaseQuickAdapter<ExtendCopyBean, BaseDataBindingHolder<ItemExtensionLayoutBinding>>? = null
    var type: Int = 1          //文案类型
    var mHasLoadedOnce = false // 页面已经加载过

    var extensionPageResult = MutableLiveData<ResultState<ExtendCopyPage>>()
    var extensionListData = MutableLiveData<MutableList<ExtendCopyBean>>()
    /**
     * 推广素材列表
     */
    fun getExtensionList(reqDateMap: HashMap<String,Any>) {
        request({mRepository.getExtendCopyList(reqDateMap)},extensionPageResult)
    }

}