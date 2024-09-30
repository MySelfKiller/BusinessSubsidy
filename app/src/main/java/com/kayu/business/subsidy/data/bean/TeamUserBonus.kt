package com.kayu.business.subsidy.data.bean

data class TeamUserBonus (
    //id		主键
    //rankTitle	String	等级标题
    //rank	Int	等级
    //teamSettleCount	Int	团队结算数量
    //teamSettleAmount	Long	团队结算金额
    //commission	Long	补贴金/分
    //createTime	String	创建时间
    //statDate	String	统计账期

    var id:Long = 0,                    //主键
    var rankTitle:String = "",          //等级标题
    var rank:Int = 0,                   //等级
    var teamSettleCount:Int = 0,        //团队结算数量
    var teamSettleAmount:Long = 0,      //团队结算金额
    var commission:Long = 0,            //补贴金/分
    var createTime:String = "",         //创建时间
    var statDate:String = "",           //统计账期
)