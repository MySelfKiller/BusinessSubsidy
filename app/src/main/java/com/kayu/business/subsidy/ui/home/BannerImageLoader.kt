package com.kayu.business.subsidy.ui.home

import android.content.Context
import android.widget.ImageView
import coil.load
import com.youth.banner.loader.ImageLoader

class BannerImageLoader : ImageLoader() {
     override fun displayImage(context: Context, path: Any, imageView: ImageView) {
         (path as String?)?.let { imageView.load(it){
             this.allowHardware(false)
         } }
    }
}