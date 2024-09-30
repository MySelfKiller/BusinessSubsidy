package com.lzy.ninegrid

import android.view.ViewGroup
import android.graphics.Bitmap
import kotlin.jvm.JvmOverloads
import android.util.TypedValue
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.rishabhharit.roundedimageview.RoundedImageView
import java.util.ArrayList

class NineGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private var tileMaxHeight = 200 // 平铺模式最大高度
    private var singleImageSize = 250 // 单张图片时的最大大小,单位dp
    private var singleImageRatio = 1.0f // 单张图片的宽高比(宽/高)

    /**
     * 设置最大图片数
     */
    var maxSize = 9 // 最大显示的图片数
    private var gridSpacing = 3 // 宫格间距，单位dp
    private var mode = MODE_FILL // 默认使用fill模式
    private var columnCount // 列数
            = 0
    private var rowCount // 行数
            = 0
    private var gridWidth // 宫格宽度
            = 0
    private var gridHeight // 宫格高度
            = 0
    private val imageViews: MutableList<RoundedImageView?>
    fun getImageViews () :MutableList<RoundedImageView?>{
        return imageViews
    }
    private var mImageInfo: List<ImageInfo?>? = null
    private var mAdapter: NineGridViewAdapter? = null
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        val totalWidth = width - paddingLeft - paddingRight
        if (mImageInfo != null && mImageInfo!!.size > 0) {
            if (mode == MODE_TILE) { //平铺模式
                if (mImageInfo!!.size == 1) {
                    gridWidth = totalWidth
                    gridHeight = tileMaxHeight
                } else if (mImageInfo!!.size == 2 || mImageInfo!!.size == 4) {
                    gridWidth = (totalWidth - gridSpacing) / 2
                    gridHeight = (tileMaxHeight * 0.7).toInt() //自定义高度（单张高度的0.7倍）
                } else {
                    gridWidth = (totalWidth - gridSpacing * 2) / 3
                    gridHeight = (tileMaxHeight * 0.5).toInt() //自定义高度
                    //                    gridHeight = gridWidth ; //宽高相等
                }
            } else { // 其它样式（朋友圈 、 QQ空间）
                if (mImageInfo!!.size == 1) {
                    gridWidth = if (singleImageSize > totalWidth) totalWidth else singleImageSize
                    gridHeight = (gridWidth / singleImageRatio).toInt()
                    //矫正图片显示区域大小，不允许超过最大显示范围
                    if (gridHeight > singleImageSize) {
                        val ratio = singleImageSize * 1.0f / gridHeight
                        gridWidth = (gridWidth * ratio).toInt()
                        gridHeight = singleImageSize
                    }
                } else {
//                gridWidth = gridHeight = (totalWidth - gridSpacing * (columnCount - 1)) / columnCount;
                    //这里无论是几张图片，宽高都按总宽度的 1/3
                    gridHeight = (totalWidth - gridSpacing * 2) / 3
                    gridWidth = gridHeight
                }
            }
            width =
                gridWidth * columnCount + gridSpacing * (columnCount - 1) + paddingLeft + paddingRight
            height =
                gridHeight * rowCount + gridSpacing * (rowCount - 1) + paddingTop + paddingBottom
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mImageInfo == null) return
        val childrenCount = mImageInfo!!.size
        for (i in 0 until childrenCount) {
            val childrenView = getChildAt(i) as RoundedImageView
            val rowNum = i / columnCount
            val columnNum = i % columnCount
            val left = (gridWidth + gridSpacing) * columnNum + paddingLeft
            val top = (gridHeight + gridSpacing) * rowNum + paddingTop
            val right = left + gridWidth
            val bottom = top + gridHeight
            childrenView.layout(left, top, right, bottom)
            if (imageLoader != null) {
                if (mImageInfo!![i]?.thumbnailUrl != null) {
                    imageLoader?.onDisplayImage(
                        context,
                        childrenView,
                        mImageInfo!![i]?.thumbnailUrl
                    )
                } else {
                    imageLoader?.onDisplayImage(context, childrenView, mImageInfo!![i]?.srcUrl)
                }
            }
        }
    }

    /**
     * 设置适配器
     */
    fun setAdapter(adapter: NineGridViewAdapter) {
        mAdapter = adapter
        var imageInfo = adapter.imageInfo
        if (imageInfo.isEmpty()) {
            visibility = GONE
            return
        } else {
            visibility = VISIBLE
        }
        var imageCount = imageInfo.size
        if (maxSize > 0 && imageCount > maxSize) {
            imageInfo = imageInfo.subList(0, maxSize)
            imageCount = imageInfo.size //再次获取图片数量
        }

        //默认是3列显示，行数根据图片的数量决定
        rowCount = imageCount / 3 + if (imageCount % 3 == 0) 0 else 1
        columnCount = 3
        //grid模式下，显示4张使用2X2模式
        if (mode == MODE_GRID) {
            if (imageCount == 4) {
                rowCount = 2
                columnCount = 2
            }
        } else if (mode == MODE_TILE) {
            if (imageCount == 2) {
                rowCount = 1
                columnCount = 2
            } else if (imageCount == 4) {
                rowCount = 2
                columnCount = 2
            }
        }

        //保证View的复用，避免重复创建
        if (mImageInfo == null) {
            for (i in 0 until imageCount) {
                val iv = getImageView(i) ?: return
                addView(iv, generateDefaultLayoutParams())
            }
        } else {
            val oldViewCount = mImageInfo!!.size
            val newViewCount = imageCount
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount, oldViewCount - newViewCount)
            } else if (oldViewCount < newViewCount) {
                for (i in oldViewCount until newViewCount) {
                    val iv = getImageView(i) ?: return
                    addView(iv, generateDefaultLayoutParams())
                }
            }
        }
        //修改最后一个条目，决定是否显示更多
        if (adapter.imageInfo.size > maxSize) {
            val child = getChildAt(maxSize - 1)
            if (child is NineGridViewWrapper) {
                child.setMoreNum(adapter.imageInfo.size - maxSize)
            }
        }
        mImageInfo = imageInfo
        requestLayout()
    }

    /**
     * 获得 ImageView 保证了 ImageView 的重用
     */
    private fun getImageView(position: Int): ImageView? {
        val imageView: RoundedImageView?
        if (position < imageViews.size) {
            imageView = imageViews[position]
        } else {
            imageView = mAdapter!!.generateImageView(context)
            imageView.setOnClickListener(OnClickListener {
                mAdapter?.imageInfo?.let { it1 ->
                    mAdapter!!.onImageItemClick(
                        context, this@NineGridView, position, it1
                    )
                }
            })
            imageViews.add(imageView)
        }
        return imageView
    }

    /**
     * 设置宫格间距
     */
    fun setGridSpacing(spacing: Int) {
        gridSpacing = spacing
    }

    /**
     * 设置只有一张图片时的宽
     */
    fun setSingleImageSize(maxImageSize: Int) {
        singleImageSize = maxImageSize
    }

    /**
     * 设置只有一张图片时的宽高比
     */
    fun setSingleImageRatio(ratio: Float) {
        singleImageRatio = ratio
    }

    interface ImageLoader {
        /**
         * 需要子类实现该方法，以确定如何加载和显示图片
         *
         * @param context   上下文
         * @param imageView 需要展示图片的ImageView
         * @param url       图片地址
         */
        fun onDisplayImage(context: Context, imageView: ImageView, url: String?)
        fun onDisplayImage(context: Context, imageView: ImageView, src: Int?)

        /**
         * @param url 图片的地址
         * @return 当前框架的本地缓存图片
         */
        fun getCacheImage(url: String?): Bitmap?
    }

    companion object {
        const val MODE_FILL = 0 //填充模式，类似于微信
        const val MODE_GRID = 1 //网格模式，类似于QQ，4张图会 2X2布局
        const val MODE_TILE = 2 //平铺网格模式
        var imageLoader //全局的图片加载器(必须设置,否者不显示图片)
                : ImageLoader? = null
    }

    init {
        val dm = context.resources.displayMetrics
        gridSpacing =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gridSpacing.toFloat(), dm)
                .toInt()
        singleImageSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, singleImageSize.toFloat(), dm)
                .toInt()
        tileMaxHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tileMaxHeight.toFloat(), dm)
                .toInt()
        val a = context.obtainStyledAttributes(attrs, R.styleable.NineGridView)
        gridSpacing =
            a.getDimension(R.styleable.NineGridView_ngv_gridSpacing, gridSpacing.toFloat()).toInt()
        singleImageSize =
            a.getDimensionPixelSize(R.styleable.NineGridView_ngv_singleImageSize, singleImageSize)
        tileMaxHeight =
            a.getDimensionPixelSize(R.styleable.NineGridView_ngv_tileMaxHeight, tileMaxHeight)
        singleImageRatio =
            a.getFloat(R.styleable.NineGridView_ngv_singleImageRatio, singleImageRatio)
        maxSize = a.getInt(R.styleable.NineGridView_ngv_maxSize, maxSize)
        mode = a.getInt(R.styleable.NineGridView_ngv_mode, mode)
        a.recycle()
        imageViews = ArrayList()
    }
}