package com.kayu.business.subsidy.ui.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.OrderStateBean
import com.kayu.business.subsidy.data.bean.SystemParamBean
import com.kayu.business.subsidy.databinding.ActivityOrderDetailBinding
import com.kayu.business.subsidy.databinding.ItemStateOrderDetailBinding
import com.kayu.business.subsidy.ui.WebViewActivity
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.kex.saveToAlbum
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.v3.CustomDialog
import com.kongzue.dialog.v3.WaitDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailActivity : BaseActivity<ActivityOrderDetailBinding,OrderDetaiViewModel>() {
    var title = "订单详情"
    var back = "返回"
    override val mViewModel: OrderDetaiViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }
    override fun initObserve() {
        mViewModel.customerResult.observe(this) { it ->
            parseState(it, {
                reqCustomerSuccess = true
                mViewModel.customerLiveData.value = it
                if (isShowDialog)
                    showDialog()
            }, {
                ToastUtils.show(it)
            })
        }
        mViewModel.orderDetailLiveData.observe(this){it->
            parseState(it,{
                mBinding.orderDetailEmpty.visibility = View.GONE
                mBinding.orderDetailLay.visibility = View.VISIBLE
                mBinding.orderDetailDate.text = it.createTime
                mBinding.orderDetailName.text = it.name
                mBinding.orderDetailPhone.text = it.phone
                mBinding.orderDetailBillingRule.text = it.settlementRule
                mBinding.orderDetailBillingMethod.text = it.settlementCycle
                mBinding.orderDetailBillingTips.text = it.settlementTips
                mBinding.orderDetailProductLogo.load(it.productLogo){
                    this.allowHardware(false)
                }
                mBinding.orderDetailProductName.text = it.productName
                mBinding.orderDetailUpdateTime.text = it.statusUpdateTime
                mBinding.orderDetailNextUpdateTime.text = it.statusNextUpdateTime
                if (!it.progressUrl.isNullOrEmpty()){
                    mBinding.orderDetailProcess.setOnClickListener(object : NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            val intent = Intent(this@OrderDetailActivity, WebViewActivity::class.java)
                            val sb = StringBuilder()
                            sb.append(it.progressUrl)
                            if (it.progressUrl!!.contains("?")) {
                                sb.append("&token=")
                            } else {
                                sb.append("?token=")
                            }
                            sb.append(SMApplication.instance.token)
                            intent.putExtra("url", sb.toString())
                            intent.putExtra("from", "")
                            startActivity(intent)
                        }

                        override fun OnMoreErrorClick() {
                        }

                    })
                    mBinding.orderDetailProcess.visibility = View.VISIBLE

                }else{
                    mBinding.orderDetailProcess.setOnClickListener(null)
                    mBinding.orderDetailProcess.visibility = View.GONE
                }

                if (null == it.statusList || it.statusList.isEmpty()){
                    mBinding.orderDetailStateLay.visibility = View.GONE
                } else{
                    createAdapter(it.statusList)
                    mBinding.orderDetailStateLay.visibility = View.VISIBLE

                }
            },{
                mBinding.orderDetailEmpty.visibility = View.VISIBLE
                mBinding.orderDetailLay.visibility = View.GONE
//                ToastUtils.show("获取数据失败")
            })
            if (mViewModel.isRefresh) {
                mBinding.orderDetailRefreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            WaitDialog.dismiss()
        }

    }

    override fun initRequestData() {
        mViewModel.getWeChatCustomer()
    }

    override fun ActivityOrderDetailBinding.initView() {
        title = intent.getStringExtra("title").toString()
        back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.orderDetailTitle.titleNameTv.text = title
        mBinding.orderDetailTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.orderDetailTitle.titleBackTv.text = back


        mViewModel.orderID = intent.getLongExtra("orderID",-1)

        mBinding.orderDetailCustomer.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                showDialog()
            }

            override fun OnMoreErrorClick() {}

        })

        mBinding.orderDetailStateRecycler.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        mBinding.orderDetailRefreshLayout.setEnableAutoLoadMore(false)
        mBinding.orderDetailRefreshLayout.setEnableLoadMore(false)
        mBinding.orderDetailRefreshLayout.setEnableLoadMoreWhenContentNotFull(false) //是否在列表不满一页时候开启上拉加载功能
        mBinding.orderDetailRefreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹
        mBinding.orderDetailRefreshLayout.setEnableOverScrollDrag(true)
        mBinding.orderDetailRefreshLayout.setOnRefreshListener {
            if (mViewModel.isRefresh) return@setOnRefreshListener
            mViewModel.isRefresh = true
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter!!.notifyDataSetChanged()
            }
            reqData()
        }
//        mBinding.orderDetailRefreshLayout.autoRefresh()
        WaitDialog.show(this@OrderDetailActivity,
            "加载中..."
        )
        mViewModel.isRefresh = true
        if (null != mViewModel.adapter) {
            mViewModel.adapter!!.data.clear()
            mViewModel.adapter!!.notifyDataSetChanged()
        }
        reqData()
    }

    private fun reqData(){
        mViewModel.getOrderDetail(mViewModel.orderID)
    }

    private fun createAdapter(list:MutableList<OrderStateBean>){
        val newList = mutableListOf<OrderStateBean>()
        newList.addAll(list)
        mViewModel.adapter = object : BaseQuickAdapter<OrderStateBean, BaseDataBindingHolder<ItemStateOrderDetailBinding>>
            (R.layout.item_state_order_detail, newList) {
            @SuppressLint("Range")
            override fun convert(
                holder: BaseDataBindingHolder<ItemStateOrderDetailBinding>,
                item: OrderStateBean
            ) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemOrderStateTitle.text = item.title
                    var repTips = ""
                    if (!item.tips.isNullOrEmpty()){
                        repTips = item.tips.replace("#","\n")

                    }
                    holder.dataBinding!!.itemOrderStateTips.text = repTips
                    LogUtil.e("hm",item.isCurrent.toString())
                    val states = arrayOf(
                        intArrayOf(android.R.attr.state_pressed),
                        intArrayOf(android.R.attr.state_focused),
                        intArrayOf(android.R.attr.state_activated),
                        intArrayOf()
                    )
                    if (item.isCurrent == 1){
                        val colors = intArrayOf(
                            resources.getColor(R.color.orange_tip,null),
                            resources.getColor(R.color.orange_tip,null),
                            resources.getColor(R.color.orange_tip,null),
                            resources.getColor(R.color.orange_tip,null)
                        )
                        val colorStateList = ColorStateList(states, colors)
                        holder.dataBinding!!.itemOrderStateTitle.setTextColor(colorStateList)
                        holder.dataBinding!!.itemOrderStateImg.background = ResourcesCompat.getDrawable(resources,R.drawable.solid_red_bg_shape,null)
                    } else{
                        val colors = intArrayOf(
                            resources.getColor(R.color.gry_bg1,null),
                            resources.getColor(R.color.gry_bg1,null),
                            resources.getColor(R.color.gry_bg1,null),
                            resources.getColor(R.color.gry_bg1,null)
                        )
                        val colorStateList = ColorStateList(states, colors)
                        holder.dataBinding!!.itemOrderStateTitle.setTextColor(colorStateList)
                        holder.dataBinding!!.itemOrderStateImg.background = ResourcesCompat.getDrawable(resources,R.drawable.solid_gry_bg_shape,null)

                    }

                }
            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(this).inflate(R.layout.empty_view_tab, null))
        mBinding.orderDetailStateRecycler.adapter = mViewModel.adapter
    }

    var isShowDialog = false
    var reqCustomerSuccess = false
    private fun showDialog() {
        if (!reqCustomerSuccess) {
            isShowDialog = true
            mViewModel.getWeChatCustomer()
            return
        }
        val customer: SystemParamBean? = mViewModel.customerLiveData.value
        if (null == customer || !reqCustomerSuccess) return
        CustomDialog.show(this@OrderDetailActivity, R.layout.dialog_qrcode_lay) { dialog, v ->
            val qrcode_iv = v.findViewById<ImageView>(R.id.shared_qrcode_iv)
            val save_btn = v.findViewById<TextView>(R.id.shared_confirm_btn)
            val cancel_btn = v.findViewById<TextView>(R.id.shared_cancel_btn)
            cancel_btn.setOnClickListener(object :NoMoreClickListener(){
                override fun OnMoreClick(view: View) {
                    dialog.doDismiss()
                }

                override fun OnMoreErrorClick() {
                }

            })
//            val compay_tv1 = v.findViewById<TextView>(R.id.shared_compay_tv1)
            // TODO: 二维码链接待确认
            qrcode_iv.load(customer.url0) {
                this.allowHardware(false)
            }
            save_btn.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    SMApplication.instance.checkPermission(this@OrderDetailActivity,
                        arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), {
                            if (null == qrcode_iv.drawable) {
                                ToastUtils.show("保存图片不存在")
                                return@checkPermission
                            }
                            val fileName: String =
                                "qr_" + System.currentTimeMillis() + ".jpg"
                            if (null != ((qrcode_iv.drawable) as BitmapDrawable).bitmap.saveToAlbum(
                                    this@OrderDetailActivity,
                                    fileName
                                )
                            ) {
                                ToastUtils.show("保存成功")
                            } else {
                                ToastUtils.show("保存失败")
                            }
                        }, {
                            ToastUtils.show("权限被拒绝，无法保存图片")
                        })
                }

                override fun OnMoreErrorClick() {
                }

            })
            isShowDialog = false
        }
            .setCancelable(true)
            .setFullScreen(false)
            .setCustomLayoutParams(
                RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
    }

}