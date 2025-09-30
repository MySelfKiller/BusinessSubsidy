package com.kayu.business.subsidy.ui.mine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.NavOptionBean
import com.kayu.business.subsidy.databinding.FragmentMineBinding
import com.kayu.business.subsidy.databinding.ItemNavOptionBinding
import com.kayu.business.subsidy.ui.WebViewActivity
import com.kayu.business.subsidy.ui.order.OrderActivity
import com.kayu.business.subsidy.ui.recruit.RecruitTeamActivity
import com.kayu.business.subsidy.ui.settlement.SettlementActivity
import com.kayu.business.subsidy.ui.team.TeamActivity
import com.kayu.business.subsidy.ui.team.TeamDetailActivity
import com.kayu.business.subsidy.ui.team.TeamSubsidyActivity
import com.kayu.utils.AppUtil.getStringAmount
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kayu.utils.kex.saveToAlbum
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.v3.CustomDialog
import com.kongzue.dialog.v3.WaitDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineFragment : BaseFragment<FragmentMineBinding, MineViewModel>() {
    var isRefresh = false

    override val mViewModel: MineViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun onResume() {
        super.onResume()
        WaitDialog.show(
            requireActivity() as AppCompatActivity,
            "加载中..."
        )
        reqData()
//        mBinding.refreshLayout.autoRefresh()
    }

    override fun FragmentMineBinding.initView() {

        mBinding.mineMyTeam.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                jumpActivity("我的团队", TeamActivity::class.java)
            }

            override fun OnMoreErrorClick() {}

        })

        mBinding.mineUserInfoLay.setOnClickListener(object : NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
//                val intent = Intent(context, CashOutActivity::class.java)
//                intent.putExtra("title", "提现")
//                intent.putExtra("back", "我的")
//                startActivity(intent)

                if (mViewModel.cashOutDetailRUL.isEmpty()){
                    ToastUtils.show("无法使用")
//                    mViewModel.getCashOutRUL()
                }else {
                    val intent = Intent(context, WebViewActivity::class.java)
                    val sb = StringBuilder()
                    sb.append(mViewModel.cashOutDetailRUL)
                    if (mViewModel.cashOutDetailRUL.contains("?")) {
                        sb.append("&token=")
                    } else {
                        sb.append("?token=")
                    }
                    sb.append(SMApplication.instance.token)
//                    sb.append("&id="+item.id)
                    intent.putExtra("url", sb.toString())
                    intent.putExtra("from", "提现")
                    startActivity(intent)
                }
            }

            override fun OnMoreErrorClick() {}

        })

        mBinding.mineTeamRecruit.setOnClickListener(object : NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                val intent = Intent(context, RecruitTeamActivity::class.java)
                intent.putExtra("title", "招募团队")
                intent.putExtra("back", "我的")
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}

        })

        mBinding.mineOptionsRecycler.layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        context?.let { its ->
            ContextCompat.getDrawable(its, R.color.divider)
                ?.let { dividerItemDecoration.setDrawable(it) }
        }
        mBinding.mineOptionsRecycler.addItemDecoration(dividerItemDecoration)
        mBinding.refreshLayout.setEnableAutoLoadMore(false)
        mBinding.refreshLayout.setEnableLoadMore(false)
        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(false) //是否在列表不满一页时候开启上拉加载功能
        mBinding.refreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹
        mBinding.refreshLayout.setEnableOverScrollDrag(true)
        mBinding.refreshLayout.setOnRefreshListener {
            if (isRefresh) return@setOnRefreshListener
            isRefresh = true
            reqData()
        }
    }

    override fun initObserve() {
        mViewModel.userDetailsResult.observe(this) { it ->
            parseState(it, { result ->
                SMApplication.instance.userDetails = result
                if (StringUtil.isEmpty(result.username)){
                    mBinding.mineUserName.text = "未实名"
                }else{
                    mBinding.mineUserName.text = result.username
                }
                mBinding.mineUserPhoto.load(result.headPic){
                    this.allowHardware(false)
                }
                mBinding.mineUserPhone.text = result.phone
                mBinding.mineUserIncomeAmount.text = getStringAmount(result.rewardAmt)
                mBinding.mineUserCashOutAmount.text = getStringAmount(result.surplusAmt)
                mBinding.userAgentRankIcon.load(result.rankIcon){
                    this.allowHardware(false)
                }
                mBinding.mineUserLevel.text = result.rankTitle
            }, {
                ToastUtils.show(it)
            })
            if (isRefresh) {
                mBinding.refreshLayout.finishRefresh()
                isRefresh = false
            }
            WaitDialog.dismiss()
        }
        mViewModel.rankDetailResult.observe(this) { it ->
            parseState(it, {
                mBinding.userRankDetail.setOnClickListener(object :NoMoreClickListener(){
                    override fun OnMoreClick(view: View) {
                        if (!it.url0.isNullOrEmpty()){
                            val intent = Intent(requireContext(), WebViewActivity::class.java)
                            val sb = StringBuilder()
                            sb.append(it.url0)
                            if (it.url0!!.contains("?")) {
                                sb.append("&token=")
                            } else {
                                sb.append("?token=")
                            }
                            sb.append(SMApplication.instance.token)
                            intent.putExtra("url", sb.toString())
                            intent.putExtra("from", "我的")
                            startActivity(intent)
                        }else {
                            ToastUtils.show("链接不存在！")
                        }
                    }

                    override fun OnMoreErrorClick() {
                    }

                })

            }, {

            })
        }

        mViewModel.navListLiveData.observe(this){
            parseState(it,{ result ->
                createAdapter(result)
            },{

            })

        }
    }

    override fun initRequestData() {

    }


    private fun reqData() {
//        mViewModel.getUserSettlementData()
        mViewModel.getUserDetail()
        mViewModel.getNavList()
        mViewModel.getRankDetail()
    }


    private fun createAdapter(list:MutableList<NavOptionBean>){
        val newList = mutableListOf<NavOptionBean>()
        newList.addAll(list)
        mViewModel.adapter = object : BaseQuickAdapter<NavOptionBean, BaseDataBindingHolder<ItemNavOptionBinding>>
            (R.layout.item_nav_option, newList) {
            @SuppressLint("Range")
            override fun convert(
                holder: BaseDataBindingHolder<ItemNavOptionBinding>,
                item: NavOptionBean
            ) {
                if (null != holder.dataBinding) {
//                    holder.dataBinding!!.itemNavOptionImg.load(item.icon){
//                        this.allowHardware(false)
//                    }
                    holder.dataBinding!!.itemNavOptionTitle.text = item.title
                    holder.dataBinding!!.itemNavOptionLay.setOnClickListener(object :NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            checkJumpAction(item)
                        }

                        override fun OnMoreErrorClick() {
                        }

                    })
                }
            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(requireContext()).inflate(R.layout.empty_view_tab, null))
        mBinding.mineOptionsRecycler.adapter = mViewModel.adapter
    }

    private fun showCustomDialog(qrUrl: String?, bgImg: String?) {

        if (null == qrUrl || qrUrl.trim() == "") {
            ToastUtils.show("暂时无法联系")
        }

        CustomDialog.show(
            requireActivity() as AppCompatActivity, R.layout.dialog_qrcode_lay
        ) { dialog, v ->
            val qrcode_iv = v.findViewById<ImageView>(R.id.shared_qrcode_iv)
            val bg_iv = v.findViewById<ImageView>(R.id.shared_bg_iv)
            val save_btn = v.findViewById<TextView>(R.id.shared_confirm_btn)
            val cancel_btn = v.findViewById<TextView>(R.id.shared_cancel_btn)
    //            val compay_tv1 = v.findViewById<TextView>(R.id.shared_compay_tv1)
            bg_iv.load(bgImg){
                this.allowHardware(false)
            }
            qrcode_iv.load(qrUrl) {
                this.allowHardware(false)
            }
            cancel_btn.setOnClickListener(object :NoMoreClickListener(){
                override fun OnMoreClick(view: View) {
                    dialog.doDismiss()
                }

                override fun OnMoreErrorClick() {
                }

            })
            save_btn.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    SMApplication.instance.checkPermission(requireActivity(),
                        arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), {
                            if (null == qrcode_iv.drawable) {
                                ToastUtils.show("保存图片不存在")
                                return@checkPermission
                            }
                            val fileName: String =
                                "qr_" + System.currentTimeMillis() + ".jpg"
                            if (null != ((qrcode_iv.drawable) as BitmapDrawable).bitmap.saveToAlbum(
                                    requireContext(),
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
        }
            .setCancelable(true)
            .setFullScreen(false).customLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }

    /**
     * 判断页面跳转
     */
    private fun checkJumpAction( navOptionBean: NavOptionBean){
        val type = navOptionBean.type
        val redirect = navOptionBean.redirect
        val title: String = navOptionBean.title
        when (type){   //类型 0:app、1:url、2:img、3:tokenUrl
            0 -> {//app
                var cla : Class<*>? = null
                when(redirect){     //myOrder	我的订单
                    "myOrder" -> {
                        cla = OrderActivity::class.java
                    }
                    "reward" -> {        //reward	收益明细
                        cla = SettlementActivity::class.java
                    }
                    "withdraw" -> {     //withdraw	提现
                        cla = CashOutActivity::class.java
                    }
                    "teamStatistics" ->{//teamStatistics	团队统计
                        cla = TeamActivity::class.java
                    }
                    "teamList" -> {//teamList	团队列表
                        cla = TeamDetailActivity::class.java
                    }
                    //todo 需要添加产品列表功能   "product" -> //product	产品列表
                    //todo 需要添加消息列表功能    message	消息列表
                    "teamSubsidy" -> {// teamSubsidy	团队补贴
                        cla = TeamSubsidyActivity::class.java
                    }
                    "teamPublicize" -> {//teamPublicize	团队推广
                        cla = RecruitTeamActivity::class.java
                    }
                    //todo 需要跳转到导航页的业绩页面   orderStatistics	订单统计
                    "settings" -> {//settings	设置
                        cla = SettingsActivity::class.java
                    }
                }

                val intent = Intent(context, cla)
                intent.putExtra("title", title)
                intent.putExtra("back", "我的")
                startActivity(intent)


            }
            1 -> {//url
                if (!redirect.isNullOrEmpty()){
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("url", redirect)
                    intent.putExtra("from", title)
                    startActivity(intent)
                }else{
                    ToastUtils.show("链接不存在！")
                }

            }
            2 -> {//img
                showCustomDialog(redirect,navOptionBean.bgImg)
            }
            3 -> {//拼接token
                if (!redirect.isNullOrEmpty()){
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    val sb = StringBuilder()
                    sb.append(redirect)
                    if (redirect.contains("?")) {
                        sb.append("&token=")
                    } else {
                        sb.append("?token=")
                    }
                    sb.append(SMApplication.instance.token)
                    intent.putExtra("url", sb.toString())
                    intent.putExtra("from", title)
                    startActivity(intent)
                }else{
                    ToastUtils.show("链接不存在！")
                }


            }

        }
    }

    private fun jumpActivity(title: String, cla: Class<*>) {
        val intent = Intent(context, cla)
        intent.putExtra("title", title)
        intent.putExtra("back", "我的")
        startActivity(intent)
    }
}