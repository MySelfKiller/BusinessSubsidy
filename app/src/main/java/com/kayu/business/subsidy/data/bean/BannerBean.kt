package com.kayu.business.subsidy.data.bean

data class BannerBean(
    //id	Long	主键
    //img	String	图片
    //url	String	跳转地址

    var img //图片
            : String = "",

    var url //跳转地址
            : String = "",
//    var bgColor //背景颜色
//            : String = "",
//
//    var type //跳转方式
//            : String = "",

    var id //主键
            : Long = 0
)