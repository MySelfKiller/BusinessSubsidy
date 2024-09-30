package com.lzy.ninegrid.preview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.NineGridView
import com.lzy.ninegrid.NineGridViewAdapter
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
class NineGridViewClickAdapter(context: Context, imageInfo: List<ImageInfo>) :
    NineGridViewAdapter(context, imageInfo) {
    private val statusHeight: Int
    override fun onImageItemClick(
        context: Context,
        nineGridView: NineGridView,
        index: Int,
        imageInfo: List<ImageInfo?>
    ) {
        for (i in imageInfo.indices) {
            val info = imageInfo[i]
            var imageView: View
            imageView = if (i < nineGridView.maxSize) {
                nineGridView.getChildAt(i)
            } else {
                //如果图片的数量大于显示的数量，则超过部分的返回动画统一退回到最后一个图片的位置
                nineGridView.getChildAt(nineGridView.maxSize - 1)
            }
            info!!.imageViewWidth = imageView.width
            info.imageViewHeight = imageView.height
            val points = IntArray(2)
            imageView.getLocationInWindow(points)
            info.imageViewX = points[0]
            info.imageViewY = points[1] - statusHeight
        }
        val intent = Intent(context, ImagePreviewActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(
            ImagePreviewActivity.IMAGE_INFO,
            imageInfo as Serializable?
        )
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, index)
        intent.putExtras(bundle)
        context.startActivity(intent)
//        (context as Activity).overridePendingTransition(0, 0)
    }

    /**
     * 获得状态栏的高度
     */
    fun getStatusHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
//        var statusHeight = -1
//        try {
//            val clazz = Class.forName("com.android.internal.R\$dimen")
//            val `object` = clazz.newInstance()
//            val height = clazz.getField("status_bar_height")[`object`].toString().toInt()
//            statusHeight = context.resources.getDimensionPixelSize(height)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return result
    }

    init {
        statusHeight = getStatusHeight(context)
    }
}