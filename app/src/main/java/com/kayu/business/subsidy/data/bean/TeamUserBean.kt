package com.kayu.business.subsidy.data.bean

data class TeamUserBean (
    //id	Int	主键
    //createTime	String	创建时间
    //username	String	用户名
    //phone	String	手机号
    //rewardAmt	Long	累计佣金/分
    //wxnum	String	微信号
    //rankTitle	String	等级标题

    var id:Long = 0,                     //主键
    val headPic:String = "",          //头像
    val createTime:String = "",          //创建时间
    val username:String = "",            //用户名
    val phone:String = "",               //手机号
    val rewardAmt:Long = 0,              //累计佣金/分
    val wxnum:String = "",               //微信号
    val rankTitle:String = "",           //等级标题
)