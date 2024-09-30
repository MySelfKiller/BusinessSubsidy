package com.hoom.library.common.http

object HttpConfig {

    const val HOST: String = "https://www.ruiqi666.cn/"
//    const val HOST: String = "http://192.168.3.31:8088/" //http://192.168.3.31:9090/sslm/spv/

    //webview关闭标识
    const val CLOSE_WEB_VIEW = "https://close/"

    const val API_GET_IMG_CODE = "api/getImgCapt"                                       //1、	获取图片验证码
    const val API_GET_SMS_CODE = "api/getSmsCapt/{phone}"                               //2、	获取短信验证码
    const val API_POST_LOGIN = "api/login"                                              //3、	登陆
    const val API_GET_LOGOUT = "api/v1/logout"                                          //4、	退出登陆

    const val API_POST_RESET_PASSWORD = "api/v1/user/resetPwd"                          //34、	修改或设置密码
    const val API_POST_FORGET_PASSWORD = "api/resetPwd"                                 //35、	忘记密码
    const val API_GET_LOG_OFF_CODE = "api/v1/smsCapt/logoff"                            //37、	获取账号注销 验证码
    const val API_GET_LOG_OFF = "api/v1/user/logoff?"                                   //38、	账号注销

    const val API_GET_BANNER_LIST = "api/v1/banner/list"                                //5、	获取Banner列表
    const val API_GET_PRODUCT_GROUP_LIST = "api/v1/product-group/list"                  //6、	获取产品组列表
    const val API_GET_HOT_PRODUCT_LIST = "api/v1/product/list/hot?"                     //7、	获取热点产品列表
    const val API_GET_PRODUCT_LIST = "api/v1/product/list?"                             //8、	获取产品列表

    const val API_GET_STATISTICS = "api/v1/order/statistics"                            //9、	获取统计数据，团队数据业务
    const val API_POST_ORDER_LIST = "api/v1/order/list"                                 //10、	获取订单列表
    const val API_GET_USER_DETAIL = "api/v1/user/getdetail"                             //11、	获取用户详情(包含奖\提\余 金额相关字段)
    const val API_POST_ORDER_DELETE = "api/v1/order/invalid?"                           //12、	订单删除作废
    const val API_POST_ORDER_DETAIL = "api/v1/order/getdetail/{id}"                     //13、	订单获取详情

//    const val API_GET_INVITE_INFO = "api/v1/user/getInviteQrCode"                       //14、	获取用户邀请二维码
    const val API_GET_INVITE_INFO = "api/v1/recruit/getDrawMaterial"                       //14、	获取用户邀请二维码
    const val API_POST_EXTEND_COPY_LIST = "api/v1/publicize/list"                       //15、	获取推广文案列表

    const val API_GET_TEAM_INFO = "api/v1/statistics/teamInfo"                          //16、	获取团队信息
    const val API_POST_TEAM_LIST = "api/v1/user/teamList"                               //17、	获取团队列表-直推
    const val API_GET_TEAM_ACHIEVEMENT = "api/v1/statistics/user/{userId}"              //18、	获取团队业绩
    const val API_POST_TEAM_SUBSIDY_LIST = "api/v1/user-rank/queryList"                 //19、	团队补贴列表

    const val API_POST_APPLY_CASH_OUT = "api/v1/withdraw/apply"                         //22、	申请提现
    const val API_POST_WITHDRAW_LIST = "api/v1/withdraw/list"                           //23、	获取提现列表
    const val API_POST_REWARD_LIST = "api/v1/reward/list"                               //24、	获取收益列表
    const val API_GET_STATISTICS_DATA = "api/v1/reward/statistics"                      //25、	获取收益统计

    const val API_GET_DEBIT_CARD_LIST = "api/v1/settlement-card/list"                   //26、	获取储蓄卡列表
    const val API_POST_BIND_DEBIT_CARD = "api/v1/settlement-card/add"                   //27、	绑定储蓄卡
    const val API_POST_UNBIND_DEBIT_CARD = "api/v1/settlement-card/del"                 //28、	解绑储蓄卡
    const val API_POST_USER_VERIFIED = "api/v1/user/realm"                              //29、	实名认证

    const val API_GET_SYS_PARAM = "api/sys-parameter/get/{type}"                        //30、	获取系统参数表
    const val API_POST_MESSAGE_LIST = "api/v1/message/list"                              //31、	获取通知列表
    const val API_POST_MSGTIP_LIST = "api/v1/bdmsg/list"                                 //33、	获取广播消息列表

    const val API_GET_NAV_LIST = "api/v1/nav/list"                                      //32、	获取导航按钮列表

    //  api/sys-parameter/app/version/2/compare?version=
    const val API_GET_APP_UPGRADE = "api/sys-parameter/app/version/2/compare?"          //40、    版本对比 检测APP升级

    const val API_GET_ACTIVITY_LIST = "api/v1/popups/list"                              //41、	获取弹窗列表
    const val API_GET_UNREAD_NUM = "api/v1/message/unreadCount"                      //42、	获取未读消息数量

    const val API_POST_ADD_WECAHT = "api/v1/user/bwxnum?"                                //37、	绑定微信号  api/v1/user/bwxnum?wxnum=123456


}