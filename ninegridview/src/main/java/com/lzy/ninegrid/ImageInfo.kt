package com.lzy.ninegrid

import java.io.Serializable

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/3/21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ImageInfo : Serializable {
    var thumbnailUrl: String? = null
    var bigImageUrl: String? = null
    var imageViewHeight = 0
    var imageViewWidth = 0
    var imageViewX = 0
    var imageViewY = 0
    var srcUrl = 0
    override fun toString(): String {
        return "ImageInfo{" +
                "imageViewY=" + imageViewY +
                ", imageViewX=" + imageViewX +
                ", imageViewWidth=" + imageViewWidth +
                ", imageViewHeight=" + imageViewHeight +
                ", bigImageUrl='" + bigImageUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}'
    }
}