package com.kayu.business.subsidy.api

import com.hoom.library.common.http.HttpConfig
import com.hoom.library.common.network.ApiResponse
import com.kayu.business.subsidy.data.bean.ActivityList
import com.kayu.business.subsidy.data.bean.BannerBean
import com.kayu.business.subsidy.data.bean.CashOutListBean
import com.kayu.business.subsidy.data.bean.DebitCardBean
import com.kayu.business.subsidy.data.bean.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 服务端API接口
 */
interface UnionAPIService {


    /**
     * 首页-产品分组列表
     */
    @GET(HttpConfig.API_GET_PRODUCT_GROUP_LIST)
    suspend fun getProductGroupList(): ApiResponse<MutableList<ProductGroup>>


    /**
     * 首页-热门产品列表
     */
    @GET(HttpConfig.API_GET_HOT_PRODUCT_LIST)
    suspend fun getHotProductList( @Query("cityName") cityName: String): ApiResponse<HotProductList>

    /**
     * 首页-产品列表 全部
     */
    @GET(HttpConfig.API_GET_PRODUCT_LIST)
    suspend fun getProductList( @Query("cityName") cityName: String): ApiResponse<MutableList<ProductItemBean>>
    /**
     * 首页-产品列表 分组
     */
    @GET(HttpConfig.API_GET_PRODUCT_LIST)
    suspend fun getProductList(@Query("groupId") groupId: Long, @Query("cityName") cityName: String): ApiResponse<MutableList<ProductItemBean>>

    /**
     * 业绩-统计数据
     */
    @GET(HttpConfig.API_GET_STATISTICS)
    suspend fun getStatistics(): ApiResponse<StatisticData>


    /**
     * 发送短信验证码
     */
    @GET(HttpConfig.API_GET_SMS_CODE)
    suspend fun sendSmsCode(@Path("phone") phone: String ): ApiResponse<Any?>


    /**
     * 提现申请
     */
    @POST(HttpConfig.API_POST_APPLY_CASH_OUT)
    suspend fun applyCashOut(@Body body : RequestBody ): ApiResponse<Any?>

    /**
     * 删除订单
     */
    @POST(HttpConfig.API_POST_ORDER_DELETE)
    suspend fun deleteOrder(@Query("id") id: Long ): ApiResponse<Any?>

    /**
     * 我的订单列表
     */
    @POST(HttpConfig.API_POST_ORDER_LIST)
    suspend fun getOrderList(@Body body : RequestBody ): ApiResponse<OrderPageData>

    /**
     * 推广文案列表
     */
    @POST(HttpConfig.API_POST_EXTEND_COPY_LIST)
    suspend fun getExtendCopyList(@Body body : RequestBody ): ApiResponse<ExtendCopyPage>

    /**
     * 获取订单详情
     */
    @GET(HttpConfig.API_POST_ORDER_DETAIL)
    suspend fun getOrderDetail(@Path("id") id: Long): ApiResponse<OrderDetailBean>


    /**
     * 提现记录列表
     */
    @POST(HttpConfig.API_POST_WITHDRAW_LIST)
    suspend fun getCashOutList(@Body body : RequestBody ): ApiResponse<CashOutListBean>

    /**
     * 收益明细列表
     */
    @POST(HttpConfig.API_POST_REWARD_LIST)
    suspend fun getSettlementList(@Body body : RequestBody ): ApiResponse<SettlementListBean>


    /**
     * 实名认证
     */
    @POST(HttpConfig.API_POST_USER_VERIFIED)
    suspend fun setUserVerified(@Body body : RequestBody ): ApiResponse<Any?>

    /**
     * 绑定储蓄卡
     */
    @POST(HttpConfig.API_POST_BIND_DEBIT_CARD)
    suspend fun addDebitCard(@Body body : RequestBody ): ApiResponse<Any?>
    /**
     * 解绑储蓄卡
     */
    @POST(HttpConfig.API_POST_UNBIND_DEBIT_CARD)
    suspend fun delDebitCard(@Body body : RequestBody ): ApiResponse<Any?>

    /**
     * 获取密码登录 图片验证码
     */
    @GET(HttpConfig.API_GET_IMG_CODE)
    suspend fun getImageCode(): ApiResponse<ImageCode>
    /**
     * 获取首页活动弹出列表
     */
    @GET(HttpConfig.API_GET_ACTIVITY_LIST)
    suspend fun getActivityList(): ApiResponse<ActivityList>
    /**
     * 获取未读信息数量
     */
    @GET(HttpConfig.API_GET_UNREAD_NUM)
    suspend fun getMsgUnreadNum(): ApiResponse<Any?>
    /**
     * 设置密码-发送短信验证码
     */
    @GET(HttpConfig.API_GET_SMS_CODE)
    suspend fun sendSetPwdSmsCode(@Path("phone") phone: String ): ApiResponse<Any?>

    /**
     * 注销账户-发送短信验证码
     */
    @GET(HttpConfig.API_GET_LOG_OFF_CODE)
    suspend fun sendLogoffSmsCode( ): ApiResponse<Any?>

    /**
     * 注销账号
     */
    @POST(HttpConfig.API_GET_LOG_OFF)
    suspend fun logoff( @Query("code") code: String ): ApiResponse<Any?>


    /**
     * 获取 我的 设置 功能列表
     */
    @GET(HttpConfig.API_GET_NAV_LIST)
    suspend fun getNavList(): ApiResponse<MutableList<NavOptionBean>>

    /**
     * 检查app是否需要升级
     */
    @GET(HttpConfig.API_GET_APP_UPGRADE)
    suspend fun checkUpgrade(@Query("version") version: String ): ApiResponse<UpgradeInfo>


    /**
     * 图片下载
     */
    @Streaming
    @GET
    suspend fun imgDownload(@Url fileUrl: String ): ResponseBody

    /**
     * 获取邀请码信息
     */
    @GET(HttpConfig.API_GET_INVITE_INFO)
    suspend fun getInviteInfo(): ApiResponse<InviteInfoBean?>

    /**
     * 用户登陆
     */
    @POST(HttpConfig.API_POST_LOGIN)
    suspend fun login(@Body body : RequestBody ): ApiResponse<LoginDataBean>


    /**
     * 忘记密码，重新设置
     */
    @POST(HttpConfig.API_POST_FORGET_PASSWORD)
    suspend fun setForgetPassword(@Body body : RequestBody ): ApiResponse<Any?>

    /**
     * 设置密码
     */
    @POST(HttpConfig.API_POST_RESET_PASSWORD)
    suspend fun setPassword(@Body body : RequestBody ): ApiResponse<Any?>

    /**
     * 首页-Banner列表
     */
    @GET(HttpConfig.API_GET_BANNER_LIST)
    suspend fun getBannerList():ApiResponse<MutableList<BannerBean>>


    /**
     * 首页-广播消息列表
     */
    @POST(HttpConfig.API_POST_MSGTIP_LIST)
    suspend fun getNoticeList(@Body body : RequestBody): ApiResponse<NoticeList>

    /**
     * 消息通知列表
     */
    @POST(HttpConfig.API_POST_MESSAGE_LIST)
    suspend fun getMessageList(@Body body : RequestBody): ApiResponse<MessageList>

    /**
     * 收益数据统计
     */
    @GET(HttpConfig.API_GET_STATISTICS_DATA)
    suspend fun getIncomeStatistics(): ApiResponse<IncomeStaBean>

    /**
     * 个人中心——用户详情
     */
    @GET(HttpConfig.API_GET_USER_DETAIL)
    suspend fun getUserDetail(): ApiResponse<UserDetails>

    /**
     * 获取系统参数表
     */
    @GET(HttpConfig.API_GET_SYS_PARAM)
    suspend fun getSysParam(@Path("type") type: Int): ApiResponse<SystemParamBean>

    /**
     * 获取储蓄卡列表
     */
    @GET(HttpConfig.API_GET_DEBIT_CARD_LIST)
    suspend fun getCardList(): ApiResponse<MutableList<DebitCardBean>>



    /**
     * 绑定微信号
     */
    @POST(HttpConfig.API_POST_ADD_WECAHT)
    suspend fun addWeCaht(@Query("wxnum") wxnum: String ): ApiResponse<Any?>


    /**
     * 我的团队——推荐人信息
     */
//    @GET(HttpConfig.API_GET_RECOMMEND_INFO)
//    suspend fun getRecommendInfo(): ApiResponse<RecommendInfoBean>

    /**
     * 我的团队——团队数据统计
     */
    @GET(HttpConfig.API_GET_TEAM_INFO)
    suspend fun getTeamInfo(): ApiResponse<TeamStatDataBean>

    /**
     * 我的团队——团队列表
     */
    @POST(HttpConfig.API_POST_TEAM_LIST)
    suspend fun getTeamList(@Body body : RequestBody): ApiResponse<TeamList>

    /**
     * 我的团队——直推用户团队业绩
     */
    @GET(HttpConfig.API_GET_TEAM_ACHIEVEMENT)
    suspend fun getTeamAchv(@Path("userId") subUserId: Long): ApiResponse<TeamAchiDataBean>

    /**
     * 19、	团队补贴列表
     */
    @POST(HttpConfig.API_POST_TEAM_SUBSIDY_LIST)
    suspend fun getTeamSubsidyList(@Body body : RequestBody): ApiResponse<TeamBonusList>

}
