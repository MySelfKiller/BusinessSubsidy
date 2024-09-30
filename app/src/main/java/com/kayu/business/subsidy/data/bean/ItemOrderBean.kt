package com.kayu.business.subsidy.data.bean

data class ItemOrderBean(
    //id	Long	主键
    //name	String	办卡人姓名
    //phone	String	办卡人手机号
    //productLogo	String	产品logo
    //productName	String	产品名称
    //createTime	String	创建时间
    //settleStatus		结算状态 0：默认 1:结算一笔 2:结算
    //state		订单状态 1:待确认 2:已结算 3:已失效
    //orderId	String	订单号
    //tag	String	订单状态-转字意
    //userApplyStatus		申请状态 0:订单初始化、7:已进件、1:初审失败、2:初审通过、5:终审失败、6:终审成功、3:激活成功、4:已首刷
    //isNewUser		是否新户 0:未更新 1:是 2:否
    //tips	String	订单提示
    //isNewUserTitle	String	是否新户-转字意
    //userApplyStatusTitle	String	申请状态-转字意

    //"id": 21,
    //                "name": "金章冀",
    //                "phone": "13717699831",
    //                "productLogo": "https://www.kakayuy.net/images/EGDUINKB10145058.jpg",
    //                "username": null,
    //                "productName": "金章冀",
    //                "createTime": "2023-02-23 17:18:44",
    //                "settleStatus": 0,
    //                "state": 3,
    //                "orderId": "f7290c617774475b875462cd81b7d448",
    //                "tag": "已失效",
    //                "userApplyStatus": 0,
    //                "isNewUser": 0,
    //                "tips": "6666",
    //                "userApplyStatusTitle": "订单初始化",
    //                "isNewUserTitle": "未更新"


    val id: Long ,
    val name: String ,              //名字
    val phone: String ,             //电话
    val productLogo: String ,       //产品头像
    val productName: String ,       //产品名称
    val createTime: String ,        //订单创建时间
//    val arriveTime: String ,        //订单结算时间
    val settleStatus: Int,          //结算状态 0：默认 1:结算一笔 2:结算
    val state: Int,                 //订单状态
    val orderId: String,              //订单号
    val tag: String ,               //订单状态-转字意
    val userApplyStatus: String,    //申请状态 0:订单初始化、7:已进件、1:初审失败、2:初审通过、5:终审失败、6:终审成功、3:激活成功、4:已首刷
    val isNewUser: String ,         //是否新户 0:未更新 1:是 2:否
    val tips: String ,              //订单提示
    val isNewUserTitle: String ,    //是否新户-转字意
    val userApplyStatusTitle: String //申请状态-转字意

)