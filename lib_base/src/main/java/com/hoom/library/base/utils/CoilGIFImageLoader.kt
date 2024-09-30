package com.hoom.library.base.utils

import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.hoom.library.base.BaseApplication

/**
 * 用于加载 Gif 的 Coil ImageLoader
 */
object CoilGIFImageLoader {

    val imageLoader = ImageLoader.Builder(BaseApplication.context)
        .componentRegistry {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder(BaseApplication.context))
            } else {
                add(GifDecoder())
            }
        }
        .build()
}