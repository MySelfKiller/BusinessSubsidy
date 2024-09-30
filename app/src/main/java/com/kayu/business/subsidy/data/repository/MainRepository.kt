package com.kayu.business.subsidy.data.repository

import com.hoom.library.common.network.ApiResponse
import com.kayu.utils.LogUtil
import com.kayu.business.subsidy.api.UnionAPIService
import com.kayu.business.subsidy.data.bean.ActivityList
import com.kayu.business.subsidy.data.bean.BannerBean
import com.kayu.business.subsidy.data.bean.CashOutListBean
import com.kayu.business.subsidy.data.bean.DebitCardBean
import com.kayu.business.subsidy.data.bean.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

/**
 * 首页数据仓库
 */
class MainRepository @Inject constructor(private val mService: UnionAPIService) {

//    suspend fun getWeChat(): ApiResponse<OldSystemParam>{
//        return mService.getWeChat()
//    }
//
//    suspend fun getCustomer(): ApiResponse<OldSystemParam>{
//        return mService.getCustomer()
//    }

    /**
     * 获取邀请码信息
     */

    suspend fun getInviteInfo(): ApiResponse<InviteInfoBean?>{
        return mService.getInviteInfo()
    }
    
    suspend fun imgDownload(fileUrl:String): ResponseBody{
        return mService.imgDownload(fileUrl)
    }

    suspend fun getBannerList(): ApiResponse<MutableList<BannerBean>>{
        return mService.getBannerList()
    }
    suspend fun getProductGroupList(): ApiResponse<MutableList<ProductGroup>>{
        return mService.getProductGroupList()
    }
    suspend fun getHotProductList(cityName: String) : ApiResponse<HotProductList>{
        return mService.getHotProductList(cityName)
    }
    suspend fun getProductList(cityName: String) : ApiResponse<MutableList<ProductItemBean>>{
        return mService.getProductList(cityName)
    }
    suspend fun getProductList(groupId: Long,cityName: String) : ApiResponse<MutableList<ProductItemBean>>{
        return mService.getProductList(groupId,cityName)
    }

    /**
     * 获取导航页-业绩-数据统计
     */
    suspend fun getStatistics() : ApiResponse<StatisticData>{
        return mService.getStatistics()
    }

    /**
     * 获取导航页-订单-订单列表数据
     */
    suspend fun getOrderList(reqDateMap: HashMap<String,Any>): ApiResponse<OrderPageData>{
        return mService.getOrderList(toRequestBody(reqDateMap))
    }

    /**
     * 推广文案列表
     */
    suspend fun getExtendCopyList(reqDateMap: HashMap<String,Any>): ApiResponse<ExtendCopyPage>{
        return mService.getExtendCopyList(toRequestBody(reqDateMap))
    }

    /**
     * 删除订单
     */
    suspend fun deleteOrder(id: Long): ApiResponse<Any?>{
        return mService.deleteOrder(id)
    }

    /**
     * 订单详情
     */
    suspend fun getOrderDetail(subUserId: Long) : ApiResponse<OrderDetailBean>{
        return mService.getOrderDetail(subUserId)
    }

    /**
     * 获取 我的 设置 功能列表
     */
    suspend fun getNavList() : ApiResponse<MutableList<NavOptionBean>>{
        return mService.getNavList()
    }











    suspend fun getCardList() : ApiResponse<MutableList<DebitCardBean>>{
        return mService.getCardList()
    }

    suspend fun getSysParam(type: Int) : ApiResponse<SystemParamBean>{
        return mService.getSysParam(type)
    }

    /**
     * 首页-广播消息列表
     */
    suspend fun getNoticeList(reqDateMap: HashMap<String,Any?>) : ApiResponse<NoticeList>{
        return mService.getNoticeList(toRequestBody(reqDateMap))
    }
    suspend fun getMessageList(reqDateMap: HashMap<String,Any?>) : ApiResponse<MessageList>{
        return mService.getMessageList(toRequestBody(reqDateMap))
    }

//    suspend fun getRecommendInfo(): ApiResponse<RecommendInfoBean>{
//        return mService.getRecommendInfo()
//    }

    suspend fun getTeamInfo(): ApiResponse<TeamStatDataBean>{
        return mService.getTeamInfo()
    }

    suspend fun getTeamList(reqDateMap: HashMap<String,Any>): ApiResponse<TeamList>{
        return mService.getTeamList(toRequestBody(reqDateMap))
    }

    suspend fun getTeamAchv(subUserId: Long): ApiResponse<TeamAchiDataBean>{
        return mService.getTeamAchv(subUserId)
    }

    suspend fun getIncomeStatistics(): ApiResponse<IncomeStaBean>{
        return mService.getIncomeStatistics()
    }
    suspend fun getUserDetail(): ApiResponse<UserDetails>{
        return mService.getUserDetail()
    }


    /**
     * 绑定微信号
     */
    suspend fun addWeCaht(weChat:String): ApiResponse<Any?>{
        return mService.addWeCaht(weChat)
    }



    suspend fun getTeamSubsidyList(reqDateMap: HashMap<String,Any>): ApiResponse<TeamBonusList>{
        return mService.getTeamSubsidyList(toRequestBody(reqDateMap))
    }

    suspend fun applyCashOut(reqDateMap: HashMap<String,Any>): ApiResponse<Any?>{
        return mService.applyCashOut(toRequestBody(reqDateMap))
    }


    suspend fun getActivityList(): ApiResponse<ActivityList>{
        return  mService.getActivityList()
    }
    suspend fun getMsgUnreadNum(): ApiResponse<Any?>{
        return  mService.getMsgUnreadNum()
    }

    /**
     * 提现记录列表
     */
    suspend fun getCashOutList(reqDateMap: HashMap<String,Any>): ApiResponse<CashOutListBean>{
        return mService.getCashOutList(toRequestBody(reqDateMap))
    }
    suspend fun getSettlementList(reqDateMap: HashMap<String,Any>): ApiResponse<SettlementListBean>{
        return mService.getSettlementList(toRequestBody(reqDateMap))
    }
    suspend fun setUserVerified(reqDateMap: HashMap<String,Any>): ApiResponse<Any?>{
        return mService.setUserVerified(toRequestBody(reqDateMap))
    }
    suspend fun addDebitCard(reqDateMap: HashMap<String,Any>): ApiResponse<Any?>{
        return mService.addDebitCard(toRequestBody(reqDateMap))
    }
    suspend fun delDebitCard(reqDateMap: HashMap<String,Any>): ApiResponse<Any?>{
        return mService.delDebitCard(toRequestBody(reqDateMap))
    }

    private fun toRequestBody( reqDateMap: HashMap<*,*>): RequestBody {
        return toJSONObject( reqDateMap ).toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

//    private fun toJSONObject(vararg params: Pair<String, Any?>): JSONObject {
//        val param = JSONObject()
//        for (i in params) {
//            val value = if (i.second == null) "" else i.second
//            param.put(i.first, value)
//        }
//        LogUtil.e("http request param：", param.toString())
//        return param
//    }

    private fun toJSONObject(reqDateMap: Map<*,*>): JSONObject {
        val param = JSONObject(reqDateMap)
        LogUtil.e("http request param：", param.toString())
        return param
    }

}