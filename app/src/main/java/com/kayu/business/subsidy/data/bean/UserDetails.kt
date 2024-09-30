package com.kayu.business.subsidy.data.bean

data class UserDetails(
    //字段	类型	说明
    //id	Long	主键
    //username	String	用户名
    //phone	String	手机号
    //rewardAmt	Long	总收益/分
    //surplusAmt	Long	账户余额/分
    //drawAmt	Long	已提金额/分
    //initPwd	Int	是否设置密码 0:未设置 1:已设置
    //idNo	String	证件号
    //master	String
    //wxnum	String	微信号
    //rank	Int	账号等级
    //rankTitle	 String	账号等级标题

    //"id": 13,
    //"username": "高尚",
    //"phone": "13717699831",
    //"rewardAmt": 786300,
    //"surplusAmt": 779000,
    //"drawAmt": 7300,
    //"initPwd": 0,
    //"idNo": "1302************38",
    //"wxnum": "1111111",
    //"headPic": "http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIzmgiap6rhW62ydqFdMrrBOVnYkKM232O5icL0JZf6xN5ddIa03c6kr7fNiaCQbicoA8kib14lpXBFDtg",
    //"rank": 3,
    //"rankTitle": "A3",
    //"rankIcon": "https://www.kakayuy.net/images/A3.png"

    val id :Long, //主键ID
    val username : String , //用户名
    val headPic : String , //头像

    val phone : String ,    //手机号

    val groupName: String , //团队名称

    val rewardAmt: Long , //  总计奖励金额

    val surplusAmt: Long , //剩余可提金额

    val drawAmt: Long , //已申提现金额

    val initPwd: Int , //是否需要设定密码 0:否 1:是

    val idNo: String , //证件号码
//    val master: Int , //是否为领导 0:不是 1：是
    val wxnum: String , //微信号
    val rank: Int, //账号等级
    val rankTitle: String, //账号等级标题
    val rankIcon: String //等级图标


)