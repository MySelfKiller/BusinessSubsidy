package com.kayu.business.subsidy.ui.home

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.common.ext.request
import com.hoom.library.common.state.ResultState
import com.kayu.business.subsidy.data.bean.BannerBean
import com.kayu.business.subsidy.data.bean.HotProductList
import com.kayu.business.subsidy.data.bean.NoticeList
import com.kayu.business.subsidy.data.bean.ProductGroup
import com.kayu.business.subsidy.data.bean.ProductItemBean
import com.kayu.business.subsidy.data.repository.MainRepository
import com.kayu.business.subsidy.databinding.ItemHomeCreditCardBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val mRepository: MainRepository) : BaseViewModel() {
    var isRefresh = false
    var adapter: BaseQuickAdapter<ProductItemBean, BaseDataBindingHolder<ItemHomeCreditCardBinding>>? = null

//    var setProductRatioResult = MutableLiveData<ResultState<ApiResponse<Any>>>()
    var productListLiveData = MutableLiveData<ResultState<MutableList<ProductItemBean>>>()
//    var hotProductListLiveData = MutableLiveData<ResultState<HotProductList>>()
    var productGroupListLiveData = MutableLiveData<ResultState<MutableList<ProductGroup>>>()
    var bannerListLiveData = MutableLiveData<ResultState<MutableList<BannerBean>>>()
    var noticeListData = MutableLiveData<ResultState<NoticeList>>()

    /**
     * 公告列表
     */
    fun getNoticeList(reqDateMap: HashMap<String,Any?>) {
        request({mRepository.getNoticeList(reqDateMap)},noticeListData)
    }

    /**
     * 产品列表 分组
     */
    fun getProductListData( groupId:Long, cityName: String ) {
        request({mRepository.getProductList(groupId,cityName)},productListLiveData)
    }
    /**
     * 产品列表 全部
     */
    fun getProductListData(  cityName: String ) {
        request({mRepository.getProductList(cityName)},productListLiveData)
    }

    /**
     * 热门产品列表
     */
//    fun getHotProductListData( cityName: String ) {
//        request({mRepository.getHotProductList(cityName)},hotProductListLiveData)
//    }
    /**
     * 产品分组
     */
    fun getProductGroupListData( ) {
        request({mRepository.getProductGroupList( )},productGroupListLiveData)
    }

    /**
     * banner列表
     */
    fun getBannerListData( ) {
        request({mRepository.getBannerList()},bannerListLiveData)
    }

}