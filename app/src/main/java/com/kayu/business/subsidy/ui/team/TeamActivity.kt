package com.kayu.business.subsidy.ui.team

import android.content.ClipboardManager
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.databinding.ActivityTeamBinding
import com.kayu.utils.AppUtil
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.v3.CustomDialog
import com.kongzue.dialog.v3.InputDialog
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamActivity : BaseActivity<ActivityTeamBinding, TeamViewModel>() {

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override val mViewModel: TeamViewModel by viewModels()

    override fun ActivityTeamBinding.initView() {
        mViewModel.title = intent.getStringExtra("title").toString()
        mViewModel.back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.teamTitle.titleNameTv.text = mViewModel.title
        mBinding.teamTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.teamTitle.titleBackTv.text = mViewModel.back

        mBinding.teamRefreshLayout.setEnableAutoLoadMore(false)
        mBinding.teamRefreshLayout.setEnableLoadMore(false)
        mBinding.teamRefreshLayout.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能

        mBinding.teamRefreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹

        mBinding.teamRefreshLayout.setEnableOverScrollDrag(true)
        mBinding.teamRefreshLayout.setOnRefreshListener(OnRefreshListener {
            if (mViewModel.isRefresh ) return@OnRefreshListener
            mViewModel.isRefresh = true
            reqTeamData()
        })

//        mBinding.teamRefreshLayout.autoRefresh()
        WaitDialog.show(this@TeamActivity,
            "加载中..."
        )
        mViewModel.isRefresh = true
        reqTeamData()

        mBinding.teamDetailBtn.setOnClickListener(object : NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                val intent = Intent(this@TeamActivity, TeamDetailActivity::class.java)
                intent.putExtra("title", "团队详情")
                intent.putExtra("back", "我的团队")
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {
            }

        })
        mBinding.teamHistoryBtn.setOnClickListener(object : NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                val intent = Intent(this@TeamActivity, TeamSubsidyActivity::class.java)
                intent.putExtra("title", "历史详情")
                intent.putExtra("back", "我的团队")
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {
            }

        })

    }

    override fun initObserve() {
        mViewModel.addWeChatResult.observe(this){ it ->
            parseState(it,{
                ToastUtils.show("添加成功！")
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
        mViewModel.teamInfoLiveData.observe(this){
            parseState(it,{ teamInfo->
                mBinding.teamDirectNum.text = teamInfo.teamInfo.total.toString()
                mBinding.teamDirectNumYes.text = "昨日+"+teamInfo.teamInfo.ydTotal.toString()
                mBinding.teamTotalNum.text = teamInfo.teamInfo.teamTotal.toString()
                mBinding.teamTotalNumYes.text = "昨日+"+teamInfo.teamInfo.ydTeamTotal.toString()

                mBinding.teamSettleNum.text = teamInfo.achievementInfo.tmSettleCount.toString()
                mBinding.teamSettleNumYes.text = "昨日+"+teamInfo.achievementInfo.ydSettleCount
                mBinding.teamTotalSettleNum.text = AppUtil.getStringAmount(teamInfo.achievementInfo.tmTeamSettleAmt)
                mBinding.teamTotalSettleNumYes.text = "昨日+"+AppUtil.getStringAmount(teamInfo.achievementInfo.tdTeamSettleAmt)

                mBinding.teamRecommendPhoto.load(teamInfo.refuserInfo?.headPic){
                    this.allowHardware(false)
                }
                val username = teamInfo.refuserInfo?.username
                mBinding.teamRecommendName.text = if ( username == "") "未实名" else teamInfo.refuserInfo?.username

                if (StringUtil.isEmpty(SMApplication.instance.userDetails?.wxnum)) {
                    InputDialog.show(this@TeamActivity, "请绑定微信号", "方便团队与您取得联系", "完成", "取消")
                        .setCancelable(false)
                        .setHintText("请输入微信号")
                        .setOnOkButtonClickListener { baseDialog, v, inputStr ->
                            if (StringUtil.isEmpty(inputStr)) {
                                ToastUtils.show("微信号不能为空")
                                return@setOnOkButtonClickListener true
                            } else {
//                                val reqDateMap = HashMap<String, Any>()
//                                reqDateMap["wxnum"] = inputStr
                                mViewModel.addWeCaht(inputStr)
                                return@setOnOkButtonClickListener false
                            }
                        }
                }
                mBinding.teamRecommendContact.setOnClickListener(object : NoMoreClickListener() {
                    override fun OnMoreClick(view: View) {
                        if (null == teamInfo.refuserInfo){
                            ToastUtils.show("推荐人不存在！")
                            return
                        }
                        showContactDialog(teamInfo.refuserInfo.username,
                            teamInfo.refuserInfo.phone, teamInfo.refuserInfo.wxnum
                        )
                    }

                    override fun OnMoreErrorClick() {}
                })
            },{err ->
                LogUtil.e("TeamActivity---teamInfoLiveData---",err.errorMsg+err.errorLog)
                ToastUtils.show("获取数据失败，稍后重试!")
            })

            if (mViewModel.isRefresh) {
                mBinding.teamRefreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
        }
    }

    override fun initRequestData() {
    }

    private fun reqTeamData(){
        mViewModel.getTeamInfo()
    }

    private fun showContactDialog(name: String?, phone: String?, wechat: String?) {
        CustomDialog.show(this@TeamActivity, R.layout.dialog_contact_details) { dialog, v ->
            val contact_name: TextView = v.findViewById(R.id.dialog_contact_name)
            val contact_phone: TextView = v.findViewById(R.id.dialog_contact_phone)
            val contact_wechat: TextView = v.findViewById(R.id.dialog_contact_wechat)
            val copy_phone: TextView = v.findViewById(R.id.dialog_contact_copy_phone)
            val copy_wechat: TextView = v.findViewById(R.id.dialog_contact_copy_wechat)
            var temName = "未实名"
            if (!StringUtil.isEmpty(name)) {
                temName = name!!
            }
            contact_name.text = temName
            contact_phone.text = phone
            var temWechat = "未上传"
            if (!StringUtil.isEmpty(wechat)) {
                temWechat = wechat!!
                contact_wechat.isEnabled = true
                copy_wechat.isClickable = true
                copy_wechat.setTextColor(ResourcesCompat.getColor(resources,R.color.colorAccent,null))
                copy_wechat.background = ResourcesCompat.getDrawable(resources,R.drawable.solid_shape,null)
                copy_wechat.setOnClickListener(object : NoMoreClickListener() {
                    override fun OnMoreClick(view: View) {
                        val cm =
                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        // 将文本内容放到系统剪贴板里。
                        cm.text = wechat
                        ToastUtils.show("微信号已复制")
                    }

                    override fun OnMoreErrorClick() {}
                })
            } else {
                copy_wechat.setTextColor(ResourcesCompat.getColor(resources,R.color.grayText1,null))
                copy_wechat.background = ResourcesCompat.getDrawable(resources,R.drawable.solid_gry_shape,null)
                contact_wechat.isEnabled = false
                copy_wechat.isClickable = false
                copy_wechat.setOnClickListener(null)
            }
            contact_wechat.text = temWechat
            copy_phone.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    if (StringUtil.isEmpty(phone)) {
                        ToastUtils.show("手机号不存在！")
                        return
                    }
                    val cm =
                        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    // 将文本内容放到系统剪贴板里。
                    cm.text = phone
                    ToastUtils.show("手机号已复制")
                }

                override fun OnMoreErrorClick() {}
            })
        }.setCancelable(true)
    }

}