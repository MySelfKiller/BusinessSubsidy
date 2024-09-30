package com.kayu.business.subsidy.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.ProductItemBean
import com.kayu.business.subsidy.data.bean.RuleBean
import com.kayu.business.subsidy.databinding.ActivityCardDetailBinding
import com.kayu.business.subsidy.databinding.ItemRulesLayBinding
import com.kayu.utils.AppUtil
import com.kayu.utils.GsonHelper
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kayu.utils.status_bar_set.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException

@AndroidEntryPoint
class CardDetailActivity : BaseActivity<ActivityCardDetailBinding,CardDetailViewModel>(){
    private var title = "标题"
    private var back = "返回"
    override val mViewModel: CardDetailViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun initObserve() {
    }

    override fun initRequestData() {
    }

    override fun ActivityCardDetailBinding.initView() {
        //标题栏
        mBinding.cardDetailTitle.titleNameTv.text = title
        mBinding.cardDetailTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.cardDetailTitle.titleBackTv.text = back

        val itemData = SMApplication.instance.itemData
        if (null != itemData) {
            val rulesList: ArrayList<RuleBean> = arrayListOf()
            try {
                val jsonArray = JSONArray(itemData.content)
                if (jsonArray.length() > 0) {
                    for (x in 0 until jsonArray.length()) {
                        val itemRule: RuleBean? = GsonHelper.fromJson(jsonArray[x].toString(), RuleBean::class.java)
                        if (itemRule != null) {
                            rulesList.add(itemRule)
                        }
                    }

                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            mBinding.cardDetailTitle.titleNameTv.text = itemData.name

            if (itemData.extraAmount > 0) {
                mBinding.detailMyPrice.text = AppUtil.getStringIntAmount(itemData.rewardAmount) + "+" + AppUtil.getStringIntAmount(
                    itemData.extraAmount
                )

            } else {
                mBinding.detailMyPrice.text = AppUtil.getStringIntAmount(itemData.rewardAmount)
            }
            mBinding.detailDialogName.text = itemData.name
            mBinding.cardDetailLogo.load(itemData.logo){
                this.allowHardware(false)
            }
            mBinding.cardDetailRecycler.layoutManager = LinearLayoutManager(this@CardDetailActivity)
            val adapter = object : BaseQuickAdapter<RuleBean, BaseDataBindingHolder<ItemRulesLayBinding>>(
                R.layout.item_rules_lay,rulesList){
                override fun convert(holder: BaseDataBindingHolder<ItemRulesLayBinding>, item: RuleBean) {
                    if (null != holder.dataBinding) {
                        holder.dataBinding!!.itemRulesName.text = item.title
                        holder.dataBinding!!.itemRulesContent.text =
                            item.content?.replace("#","\n") ?: ""
                    }
                }

            }
            adapter.setEmptyView(LayoutInflater.from(this@CardDetailActivity).inflate(R.layout.empty_view_tab, null))
            mBinding.cardDetailRecycler.adapter = adapter
            mBinding.cardDetailOkBtn.setOnClickListener { v1: View? ->
                if (StringUtil.isEmpty(itemData.applyUrl) || StringUtil.isEmpty(itemData.spreadPoster)) {
                    ToastUtils.show("无法生成二维码")
                } else {
                    val intent = Intent(this@CardDetailActivity, SharedActivity::class.java)
                    intent.putExtra("url", itemData.spreadPoster)
                    intent.putExtra("id", itemData.id)
                    intent.putExtra("applyUrl", itemData.applyUrl)
                    intent.putExtra("back", "首页")
                    intent.putExtra("title", "二维码分享")
                    startActivity(intent)
                }
            }
        }

    }

}