package com.kayu.business.subsidy.data.bean

data class ProductItemBean(
    //"id": 21,
    //                "name": "民生初审版-饿了么",
    //                "logo": "https://www.kakayuy.net/images/ANLEUWHZ15907834.jpg",
    //                "spreadPosters": null,
    //                "rewardAmount": 10000,
    //                "demotedAmount": 0,
    //                "extraAmount": 0,
    //                "sort": 9948,
    //                "tips": "结算提醒：初审通过后实时结算佣金",
    //                "createTime": "2023-03-17 16:08:24",
    //                "settlementCycle": "实时结算",
    //                "settlementRules": "初审通过",
    //                "content": "[{\"title\":\"支持城市\",\"content\":\"所有地区均可办理\"},{\"title\":\"结算周期\",\"content\":\"初审通过后实时结算佣金\"},{\"title\":\"结算规则\",\"content\":\"首次申请民生银行信用卡，收到批卡短信通知，算成功办理此业务（注：此卡是饿了么联名卡，申请时需要登录饿了么账号）\"}]",
    //                "inputLevel": "1,2,3",
    //                "applyUrl": "https://www.kakayuy.net/a.html"
    //字段	类型	说明
    //id	Long	主键id
    //name	String	产品名称
    //logo	String	产品logo
    //rewardAmount	Long	正常佣金/分
    //demotedAmount	Long	降级佣金/分
    //extraAmount	Long	额外佣金/分
    //tips	String	结算提示
    //createTime	String	创建时间
    //settlementCycle	String	结算周期
    //settlementRules	String	结算规则
    //content	String	结算说明
    //applyUrl	String	分享链接
    var id //主键id
    : Long = 0,
    var name //产品名称
    : String = "",
    var logo //产品Logo
    : String = "",
    var rewardAmount    //奖励金额(正常所得金额)/分
    : Long = 0,
    var demotedAmount   //降级金额(由金卡将为普卡所得金额)/分
    : Long = 0,
    var extraAmount     //额外金额(首刷奖励，激活奖励)/分
    : Long = 0,
    var tips            //结算提示
    : String = "",
    var createTime      //创建时间
    : String = "",
    var settlementCycle //结算周期
    : String = "",
    var settlementRules //结算规则
    : String = "",
    var applyUrl //分享链接
    : String = "",
    val spreadPoster: String = "",
    var content //详细说明
    : String = ""

)