package com.kayu.business.subsidy.data.bean

data class ExtendCopyPage (
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
    var list: MutableList<ExtendCopyBean>,
    var pages: Int = 0,
    var total: Int = 0
)