package com.kayu.business.subsidy.data.bean

data class RankData(
    //	rank		等级
    //	beginCount		开始数量
    //	endCount		结束数量
    //	isCurrent		是否当前等级 0:否 1:是


    var	rank:String = "",		//等级
    var	beginCount:Int = 0,		//开始数量
    var	endCount:Int = 0,	    //结束数量
    var	isCurrent:Int = 0,	    //是否当前等级 0:否 1:是

)