package com.lzy.ninegrid

import android.content.Context
import android.widget.ImageView
import com.rishabhharit.roundedimageview.RoundedImageView
import com.rishabhharit.roundedimageview.RoundedImageView.Companion.ALL_ROUNDED_CORNERS_VALUE
import java.io.Serializable

abstract class NineGridViewAdapter(
    protected var context: Context,
    var imageInfo: List<ImageInfo>
) : Serializable {
    /**
     * 如果要实现图片点击的逻辑，重写此方法即可
     *
     * @param context      上下文
     * @param nineGridView 九宫格控件
     * @param index        当前点击图片的的索引
     * @param imageInfo    图片地址的数据集合
     */
    open fun onImageItemClick(
        context: Context,
        nineGridView: NineGridView,
        index: Int,
        imageInfo: List<ImageInfo?>
    ) {
    }

    /**
     * 生成ImageView容器的方式，默认使用NineGridImageViewWrapper类，即点击图片后，图片会有蒙板效果
     * 如果需要自定义图片展示效果，重写此方法即可
     *
     * @param context 上下文
     * @return 生成的 ImageView
     */
    fun generateImageView(context: Context?): RoundedImageView {
//        val imageView = NineGridViewWrapper(context)
        val imageView =  RoundedImageView(context!!)
        imageView.setCornerRadius(10)
        imageView.setReverseMask(false)
        imageView.setRoundedCorners(ALL_ROUNDED_CORNERS_VALUE)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(R.drawable.ic_default_color)
        return imageView
    }

    fun setImageInfoList(imageInfo: List<ImageInfo>) {
        this.imageInfo = imageInfo
    }
}