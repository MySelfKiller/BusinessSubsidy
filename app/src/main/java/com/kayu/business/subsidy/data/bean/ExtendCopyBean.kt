package com.kayu.business.subsidy.data.bean

data class ExtendCopyBean (
    //"total": 11,
    //        "list": [
    //            {
    //                "id": 1,
    //                "headPic": "哈哈",
    //                "author": "哈哈哈",
    //                "content": "这是一个图片这是一个图片",
    //                "createTime": "2023-08-02 09:16:56",
    //                "imgList": [
    //                    {
    //                        "img": "https://www.ws101.cn/images/bg2.png",
    //                        "isQrcode": 0
    //                    }
    //                ]
    //            }
    //        ],
    //        "pages": 11

    var id: Long = 0,
    var headPic: String = "",
    var author: String = "",
    var content: String = "",
    var createTime: String = "",
    var imgList: MutableList<ExtendImgBean>? = null
)