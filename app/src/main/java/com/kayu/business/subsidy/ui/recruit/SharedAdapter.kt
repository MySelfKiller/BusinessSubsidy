package com.kayu.business.subsidy.ui.recruit

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.ExtendCopyBean
import com.kayu.business.subsidy.databinding.ItemExtensionLayoutBinding
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kayu.utils.UnicodeUtils
import com.kayu.utils.kex.saveToAlbum
import com.kongzue.dialog.v3.MessageDialog
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.preview.NineGridViewClickAdapter

class SharedAdapter(val activity: FragmentActivity,list:MutableList<ExtendCopyBean>) : BaseQuickAdapter<ExtendCopyBean, BaseDataBindingHolder<ItemExtensionLayoutBinding>>(
    R.layout.item_extension_layout, list
) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemExtensionLayoutBinding>,
        item: ExtendCopyBean
    ) {
        if (null != holder.dataBinding) {

            holder.dataBinding!!.itemExtensionNameTv.text = item.author
            if (!StringUtil.isEmpty(item.headPic)){
                holder.dataBinding!!.itemExtensionPicIm.load(item.headPic){
                    this.allowHardware(false)
                }
            }
//            holder.dataBinding!!.itemExtensionTimeTv.text = item.createTime
//            val sss = "\\u7701\\u7701\\u8054\\u76df\\u4f18\\u60e0\\u6298\\u6263\\u5361.\\u5e95\\u85aa4000\\u62db\\u4ee3\\u7406 \\u8f7b\\u8d44\\u4ea7\\u521b\\u4e1a\\u9879\\u76ee\\u65b0\\u98ce\\u53e3\\uff0c\\u95e8\\u69db\\u4f4e\\uff0c\\u9002\\u5408\\u5404\\u79cd\\u6d88\\u8d39\\u7fa4\\u4f53,\\u5168\\u56fd\\u63a8\\u5e7f,\\u4ea4\\u6613\\u4e0d\\u505c,\\u5206\\u6da6\\u4e0d\\u6b62 "
//            holder.dataBinding!!.itemExtensionContentTv.text = UnicodeUtils.encode(item.content).replace("\\n", "\n")
//            LogUtil.e("hm",item.content)
            holder.dataBinding!!.itemExtensionContentTv.text = item.content.replace("\\n", "\n")
            if (null != item.imgList && item.imgList!!.size >0) {
                val imageInfo = ArrayList<ImageInfo>()
                for (imageDetail in item.imgList!!) {
                    val info = ImageInfo()
//                    LogUtil.e("图片地址",imageDetail.img)
                    info.thumbnailUrl = imageDetail.img
                    imageInfo.add(info)
                }
                holder.dataBinding!!.nglImages.setAdapter(NineGridViewClickAdapter(activity,imageInfo))
            }

            holder.dataBinding!!.itemExtensionAskLay.setOnClickListener(object : NoMoreClickListener(){
                override fun OnMoreClick(view: View) {
//                    val pics: Array<String> = sumExtensionList.get(position).pic
                    SMApplication.instance.checkPermission(
                        activity ,
                        arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), {
                            if (item.imgList.isNullOrEmpty()) {

                            }else{
                                val imgViewList = holder.dataBinding!!.nglImages.getImageViews()
                                if (null != imgViewList){
                                    for ( i in item.imgList!!.indices){
                                        val bitmapFull: Bitmap = (imgViewList[i]?.drawable as BitmapDrawable).bitmap
                                        val temImgName: Array<String> =
                                            item.imgList!![i].img.split("/".toRegex()).toTypedArray()
                                        val fileName = "qr_" + temImgName[temImgName.size - 1]

                                        if (null !=bitmapFull.saveToAlbum(activity,fileName)) {
                                            if (i == imgViewList.size-1){
                                                ToastUtils.show("保存成功")
                                            }
                                        } else {
                                            ToastUtils.show("保存失败")
                                            break
                                        }
                                    }

                                }
                            }
                            MessageDialog.show(activity as AppCompatActivity, "请打开微信分享", "分享内容已复制,图片已保存到相册。", "分享", "取消")
                                .setOkButton { baseDialog, v ->
                                    val cm = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    // 将文本内容放到系统剪贴板里。
                                    cm.text = item.content.replace("\\n", "\n")
                                    val url = "weixin://"
                                    try {
                                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

                                    } catch (exception: ActivityNotFoundException){
                                        ToastUtils.show("没有安装微信APP！")
                                    }
                                    false
                                }

                        },{
                            ToastUtils.show("权限被拒绝，无法保存图片")
                        })

                }

                override fun OnMoreErrorClick() {
                }

            })
        }
    }
}