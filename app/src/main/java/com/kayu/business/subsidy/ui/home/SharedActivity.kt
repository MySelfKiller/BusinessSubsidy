package com.kayu.business.subsidy.ui.home

import android.Manifest
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewModelScope
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.databinding.ActivitySharedBinding
import com.kayu.business.subsidy.utils.ImageUtil
import com.kayu.business.subsidy.utils.getImageBitmapByUrl
import com.kayu.business.subsidy.view.OnPageSelectListener
import com.kayu.utils.*
import com.kayu.utils.kex.saveToAlbum
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.king.zxing.util.CodeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class SharedActivity : BaseActivity<ActivitySharedBinding,SharedViewModel>() {
    private var title = "标题"
    private var back = "返回"
    private var imgUrl: String? = null//需要显示的图片地址
    private var applyUrl: String? = null//二维码链接地址
    private var productId: Long = -1
    private var sharedUrl: String? = null
    private val viewList = mutableListOf<ImageView>()
    private var selectImgIndex = -1

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override val mViewModel: SharedViewModel by viewModels()

    override fun ActivitySharedBinding.initView() {
        title = intent.getStringExtra("title").toString()
        back = intent.getStringExtra("back").toString()
        imgUrl = intent.getStringExtra("url")
        productId = intent.getLongExtra("id", -1)
        applyUrl = intent.getStringExtra("applyUrl")

        //标题栏
        mBinding.sharedTitle.titleNameTv.text = title
        mBinding.sharedTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.sharedTitle.titleBackTv.text = back

    }

    override fun initObserve() {
        mViewModel.userDetailsResult.observe(this) { it ->
            parseState(it, {
                //生成二维码
                if (null == it.id || StringUtil.isEmpty(imgUrl) || StringUtil.isEmpty(applyUrl)){
                    LogUtil.e("hm","无法生成分享二维码，用户ID:"+it.toString()+",imgUrl:"+imgUrl+",applyUrl:"+applyUrl)
                }else{
                    selectImgIndex = 0
                    val rad = Random().nextInt(9000) + 1000
                    sharedUrl = applyUrl + "?userId=" + it.id + "&id=" + productId + "&" + rad
                    mViewModel.viewModelScope.launch {
                        val bitmapQrCode: Bitmap? = CodeUtils.createQRCode(sharedUrl ,280)
                        val resource = imgUrl?.let { getImageBitmapByUrl(it) }
                        val bitmapFull: Bitmap? = ImageUtil.compoundBitmap(resource, bitmapQrCode,0,0)

                        if (bitmapFull != null) {
                            val imageView = ImageView(this@SharedActivity)
                            val etParam = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            imageView.layoutParams = etParam
                            imageView.setImageBitmap(bitmapFull)
                            viewList.add(imageView)
                        }
                        if (null != bitmapQrCode ) {
                            val imageView = ImageView(this@SharedActivity)
                            val etParam = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            imageView.layoutParams = etParam
                            imageView.setImageBitmap(bitmapQrCode)
                            viewList.add(imageView)
                        }
                        withContext(Dispatchers.Main) {
                            if (viewList.isNotEmpty()) mBinding.sharedPosterViewPager.setViewList(viewList)
                        }
                    }

                    mBinding.sharedPosterViewPager.setOnPageSelectListener(object :
                        OnPageSelectListener {
                        override fun select(position: Int) {
                            selectImgIndex = position
                        }
                    })


                    mBinding.sharedUrlBtn.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            // 将文本内容放到系统剪贴板里。
                            val cm =
                                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            // 将文本内容放到系统剪贴板里。
                            cm.text = sharedUrl
                            ToastUtils.show("分享链接已复制")
                        }

                        override fun OnMoreErrorClick() {}
                    })

                    mBinding.sharedSaveBtn.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            SMApplication.instance.checkPermission(this@SharedActivity,
                                arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), {
//                                    if (null !=mBinding.sharedPosterIv.drawable){
//                                        val bitmapFull: Bitmap = mBinding.sharedPosterIv.drawable.toBitmap()
//                                        val temImgName: Array<String> = imgUrl!!.split("/".toRegex()).toTypedArray()
//                                        val fileName = "qr_" + temImgName[temImgName.size - 1]
//
//                                        if (null !=bitmapFull.saveToAlbum(this@SharedActivity,fileName)) {
//                                            ToastUtils.show("保存成功")
//                                        } else {
//                                            ToastUtils.show("保存失败")
//                                        }
//                                    }else{
//                                        ToastUtils.show("图片不存在，稍后重试！")
//                                    }

                                    viewList.get(selectImgIndex).setDrawingCacheEnabled(true)
                                    val bitmapFull: Bitmap = (viewList[selectImgIndex].drawable as BitmapDrawable).bitmap
                                    val temImgName: Array<String> = imgUrl!!.split("/".toRegex()).toTypedArray()
                                    val fileName = "qr_"+selectImgIndex+"_" + temImgName[temImgName.size - 1]

                                    if (null !=bitmapFull.saveToAlbum(this@SharedActivity,fileName)) {
                                        ToastUtils.show("保存成功")
                                    } else {
                                        ToastUtils.show("保存失败")
                                    }
                                    viewList.get(selectImgIndex).setDrawingCacheEnabled(false)


                                }, {
                                    ToastUtils.show("权限被拒绝，无法保存图片")
                                })
                        }

                        override fun OnMoreErrorClick() {}
                    })
                }

            }, {
                ToastUtils.show(it)
                finish()
            })
        }
    }

    override fun initRequestData() {
        mViewModel.getUserDetail()
    }

}