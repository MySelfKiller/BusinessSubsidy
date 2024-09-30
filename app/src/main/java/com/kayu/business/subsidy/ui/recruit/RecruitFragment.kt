package com.kayu.business.subsidy.ui.recruit

import android.Manifest
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.databinding.FragmentRecruitBinding
import com.kayu.business.subsidy.utils.ImageUtil
import com.kayu.business.subsidy.utils.getImageBitmapByUrl
import com.kayu.business.subsidy.view.OnPageSelectListener
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.kex.saveToAlbum
import com.king.zxing.util.CodeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@AndroidEntryPoint
class RecruitFragment : BaseFragment<FragmentRecruitBinding, RecruitViewModel>() {
    override val mViewModel: RecruitViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    private val viewList = mutableListOf<ImageView>()
    var selectImgIndex = -1
    override fun initObserve() {
        mViewModel.inviteInfoDataResult.observe(this){ it->
            parseState(it,{
                if (null != it){
                    val inviteInfoData = it
//                    val xywh: List<String> = inviteInfoData.position?.split(",") ?: return@parseState
                    selectImgIndex = 0
                    mViewModel.viewModelScope.launch {
//                        if (xywh[2].toInt() == 0){
                            for (url in inviteInfoData.imgList){
                                val resource = requireContext().getImageBitmapByUrl(url)
                                if (null != resource ) {
                                    val imageView = ImageView(requireContext())
                                    val etParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    imageView.layoutParams = etParam
                                    imageView.setImageBitmap(resource)
                                    viewList.add(imageView)
                                }
                            }
                        withContext(Dispatchers.Main) {
                            if (viewList.isNotEmpty()) mBinding.recruitTeamViewPager.setViewList(viewList)
                        }

                    }


                    mBinding.recruitTeamViewPager.setOnPageSelectListener(object :
                        OnPageSelectListener {
                        override fun select(position: Int) {
                            selectImgIndex = position
                        }
                    })


                    mBinding.recruitSaveImgBtn.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            SMApplication.instance.checkPermission(requireActivity(),
                                arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), {
                                    viewList.get(selectImgIndex).setDrawingCacheEnabled(true)
                                    val bitmapFull: Bitmap = (viewList[selectImgIndex].drawable as BitmapDrawable).bitmap
                                    val temImgName: Array<String> =
                                        inviteInfoData.imgList[selectImgIndex].split("/".toRegex()).toTypedArray()
                                    val fileName = "qr_" + temImgName[temImgName.size - 1]

                                    if (null !=bitmapFull.saveToAlbum(requireContext(),fileName)) {
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
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
    }

    override fun initRequestData() {
        mViewModel.getInviteInfo()
    }

    override fun FragmentRecruitBinding.initView() {

    }


}