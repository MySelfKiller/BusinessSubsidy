package com.kayu.business.subsidy.data.bean

data class LoginDataBean(
    //phone	String	否	手机号(密码,验证码登录必传)
    //password	String	否	密码(密码登录必传)
    //code	String	否	验证码(验证码、微信登录必传)
    //way	Int	是	登录方式 1:密码 2:验证码 3:微信
    //openId	String	是	绑定openId (微信登录时status返回10104 data 值为openId 注:必须为微信登录返回的10104 验证码登录也可能返回10104)

    //username		String	用户名
    //sign		String	会话签名,暂时没用
    //lastLoginTime		String	上次登陆时间(null 为首次登陆)
    //master		Integer	是否为领导 0:不是 1：是
    //initPwd		Integer	是否需要设定密码 0:否 1:是
    //id		Long	用户id
    //idNo		String	证件号码

    var id //用户id
    : Long = 0,
    var username //用户名
    : String = "",
    var token //会话签名,暂时没用
    : String = "",
    var lastLoginTime //上次登陆时间(null 为首次登陆)
    : String = "",
    var master //是否为领导 0:不是 1：是
    : Int = 0,
    var initPwd //是否需要设定密码 0:否 1:是
    : Int = 0,
    var groupName //团队名称
    : String = "",
    var idNo //证件号码
    : String = ""

)