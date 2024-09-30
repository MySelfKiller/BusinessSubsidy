package com.kayu.business.subsidy.data.bean

class InviteInfoBean (
    //"ino": null,
    //"username": null,
    //"phone": null,
    //"url": "http://192.168.3.41:8080/#/AddUser?u=13&r=100",
    //"imgList": [
    //"https://www.ws101.cn/images/qrcode/202212/9d869950498549a49de0a1305bf93ee2.png"
    //]
    // TODO: 和后台确认是否需要修改
    var ino: String? = null,
    var username: String? = null,
    var phone: String? = null,
    var imgList //背景图url数组
            : Array<String>,
    var url //生成的二维码url数据
            : String? = null,
)