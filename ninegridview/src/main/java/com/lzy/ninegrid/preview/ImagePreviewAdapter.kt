package com.lzy.ninegrid.preview

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.PagerAdapter
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener
import uk.co.senab.photoview.PhotoView
import android.graphics.Bitmap
import com.lzy.ninegrid.preview.ImagePreviewActivity
import android.app.Activity
import android.widget.RelativeLayout
import com.lzy.ninegrid.preview.ImagePreviewAdapter
import android.os.Bundle
import android.widget.TextView
import android.util.DisplayMetrics
import android.content.Intent
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import kotlin.jvm.JvmOverloads
import android.view.View.MeasureSpec
import android.util.TypedValue
import android.content.res.TypedArray
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextPaint
import android.graphics.PorterDuff
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import com.lzy.ninegrid.*

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/3/21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ImagePreviewAdapter(private val context: Context, private val imageInfo: List<ImageInfo>) :
    PagerAdapter(), OnPhotoTapListener {
    var primaryItem: View? = null
        private set

    override fun getCount(): Int {
        return imageInfo.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        primaryItem = `object` as View
    }

    val primaryImageView: ImageView
        get() = primaryItem!!.findViewById<View>(R.id.pv) as ImageView

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photoview, container, false)
        val pb = view.findViewById<View>(R.id.pb) as ProgressBar
        val imageView = view.findViewById<View>(R.id.pv) as PhotoView
        val info = imageInfo[position]
        imageView.onPhotoTapListener = this
        showExcessPic(info, imageView)

        //如果需要加载的loading,需要自己改写,不能使用这个方法
        if (info.thumbnailUrl != null) {
            NineGridView.imageLoader?.onDisplayImage(view.context, imageView, info.thumbnailUrl!!)
        } else {
            NineGridView.imageLoader?.onDisplayImage(view.context, imageView, info.srcUrl)
        }

//        pb.setVisibility(View.VISIBLE);
//        Glide.with(context).load(info.bigImageUrl)//
//                .placeholder(R.drawable.ic_default_image)//
//                .error(R.drawable.ic_default_image)//
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        pb.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        pb.setVisibility(View.GONE);
//                        return false;
//                    }
//                }).into(imageView);
        container.addView(view)
        return view
    }

    /** 展示过度图片  */
    private fun showExcessPic(imageInfo: ImageInfo, imageView: PhotoView) {
        //先获取大图的缓存图片
        var cacheImage: Bitmap? = NineGridView.imageLoader?.getCacheImage(imageInfo.bigImageUrl)
        //如果大图的缓存不存在,在获取小图的缓存
        if (cacheImage == null) cacheImage = NineGridView.imageLoader?.getCacheImage(imageInfo.thumbnailUrl)
        //如果没有任何缓存,使用默认图片,否者使用缓存
        if (cacheImage == null) {
            if (imageInfo.srcUrl != 0) {
                imageView.setImageResource(imageInfo.srcUrl)
            } else {
                imageView.setImageResource(R.drawable.ic_default_color)
            }
        } else {
            imageView.setImageBitmap(cacheImage)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    /** 单击屏幕关闭  */
    override fun onPhotoTap(view: View, x: Float, y: Float) {
        (context as ImagePreviewActivity).finishActivityAnim()
    }
}