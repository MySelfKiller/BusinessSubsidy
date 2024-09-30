package com.kayu.business.subsidy.ui.team

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.base.ktx.getViewId
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.state.ResultState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.TeamAchiDataBean
import com.kayu.business.subsidy.data.bean.TeamUserBean
import com.kayu.business.subsidy.databinding.ActivityTeamDetailBinding
import com.kayu.business.subsidy.databinding.ItemTeamDetailLayBinding
import com.kayu.utils.AppUtil
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TeamDetailActivity : BaseActivity<ActivityTeamDetailBinding, TeamDetailViewModel>() {
    override val mViewModel: TeamDetailViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun ActivityTeamDetailBinding.initView() {
        mViewModel.title = intent.getStringExtra("title").toString()
        mViewModel.back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.teamDetailTitle.titleNameTv.text = mViewModel.title
        mBinding.teamDetailTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.teamDetailTitle.titleBackTv.text = mViewModel.back

        mBinding.refreshLayout.setEnableAutoLoadMore(false)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能

        mBinding.refreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹

        mBinding.refreshLayout.setEnableOverScrollDrag(true)
        mBinding.refreshLayout.setOnRefreshListener(OnRefreshListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnRefreshListener
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter?.notifyDataSetChanged()
            }
            reqTeamList()

        })
        mBinding.refreshLayout.setOnLoadMoreListener(OnLoadMoreListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnLoadMoreListener
            mViewModel.isLoadMore = true
            mViewModel.pageIndex += 1
            reqTeamList()
        })

        mBinding.teamDetailSearchDelete.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                mViewModel.searchKey = ""
                mBinding.teamDetailSearchEdt.setText("")
                mBinding.teamDetailSearchDelete.visibility = View.INVISIBLE
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
                imm.hideSoftInputFromWindow(view.windowToken, 0) //强制隐藏键盘
//                mBinding.refreshLayout.autoRefresh()
                WaitDialog.show(this@TeamDetailActivity,
                    "加载中..."
                )
                mViewModel.isRefresh = true
                mViewModel.pageIndex = 1
                if (null != mViewModel.adapter) {
                    mViewModel.adapter!!.data.clear()
                    mViewModel.adapter?.notifyDataSetChanged()
                }
                reqTeamList()
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.idTagTeamSearch.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                mBinding.teamDetailSearchEdt.isFocusable = true
                mBinding.teamDetailSearchEdt.isFocusableInTouchMode = true
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.teamDetailSearchEdt.inputType = InputType.TYPE_CLASS_TEXT
        mBinding.teamDetailSearchEdt.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mViewModel.searchKey = mBinding.teamDetailSearchEdt.text.toString()
                if (StringUtil.isEmpty(mViewModel.searchKey)) {
                    ToastUtils.show("请输入客户姓名/手机号搜索订单")
                } else {
                    val inputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isActive) {
                        inputMethodManager.hideSoftInputFromWindow(
                            mBinding.teamDetailSearchEdt.windowToken, 0
                        )
//                        mBinding.refreshLayout.autoRefresh()
                        WaitDialog.show(this@TeamDetailActivity,
                            "加载中..."
                        )
                        mViewModel.isRefresh = true
                        mViewModel.pageIndex = 1
                        if (null != mViewModel.adapter) {
                            mViewModel.adapter!!.data.clear()
                            mViewModel.adapter?.notifyDataSetChanged()
                        }
                        reqTeamList()
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }
        mBinding.teamDetailSearchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (StringUtil.isEmpty(s.toString().trim { it <= ' ' })) {
                    mViewModel.searchKey = ""
                    mBinding.teamDetailSearchDelete.visibility = View.INVISIBLE
                } else {
                    mBinding.teamDetailSearchDelete.visibility = View.VISIBLE
                }
            }
        })


        mBinding.teamDetailListRecycler.layoutManager = LinearLayoutManager(this@TeamDetailActivity)
//        mBinding.refreshLayout.autoRefresh()
        WaitDialog.show(this@TeamDetailActivity,
            "加载中..."
        )
        mViewModel.isRefresh = true
        mViewModel.pageIndex = 1
        if (null != mViewModel.adapter) {
            mViewModel.adapter!!.data.clear()
            mViewModel.adapter?.notifyDataSetChanged()
        }
        reqTeamList()
    }

    override fun initObserve() {
        mViewModel.teamUserListResult.observe(this){ it->
            parseState(it,{
                val listNewData = mutableListOf<TeamUserBean>()
                it.list?.let { it1 -> listNewData.addAll(it1) }
                if (null == mViewModel.teamUserListLiveData.value ) {
                    mViewModel.teamUserListLiveData.value = mutableListOf()
                } else {
                    if (mViewModel.isRefresh) {
                        mViewModel.teamUserListLiveData.value?.clear()
                    }
                }
                mViewModel.teamUserListLiveData.value?.addAll(listNewData)
                loadListData(mViewModel.teamUserListLiveData.value as MutableList<TeamUserBean>)
            },{
                LogUtil.e("TeamSubsidyActivity---teamSubsidyListResult---",it.errorMsg+it.errorLog)
                ToastUtils.show("获取数据失败，稍后重试!")
            })
            if (mViewModel.isRefresh) {
                mBinding.refreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            if (mViewModel.isLoadMore) {
                mBinding.refreshLayout.finishLoadMore()
                mViewModel.isLoadMore = false
            }
            WaitDialog.dismiss()
        }
    }

    override fun initRequestData() {
    }

    private fun reqTeamList(){
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = mViewModel.pageIndex
        reqDateMap["pageSize"] = 10
        reqDateMap["phone"] = mViewModel.searchKey
        mViewModel.getTeamUserList(reqDateMap)
    }

    private fun loadListData(list:MutableList<TeamUserBean>){
        if (null != mViewModel.adapter) {
            mViewModel.adapter?.data?.clear()
            mViewModel.adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<TeamUserBean>){
        val newList = mutableListOf<TeamUserBean>()
        newList.addAll(list)
        mViewModel.adapter = object :
            BaseQuickAdapter<TeamUserBean, BaseDataBindingHolder<ItemTeamDetailLayBinding>>(
                R.layout.item_team_detail_lay, newList
            ) {

            @SuppressLint("SetTextI18n")
            override fun convert(holder: BaseDataBindingHolder<ItemTeamDetailLayBinding>, item: TeamUserBean) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemTeamUserPhoto.load(item.headPic){
                        this.allowHardware(false)
                    }
                    holder.dataBinding!!.itemTeamUserId.text = "推荐号："+item.phone
                    holder.dataBinding!!.itemTeamUserName.text = item.username
                    holder.dataBinding!!.itemTeamUserLevel.text = item.rankTitle
                    holder.dataBinding!!.itemTeamUserTime.text = item.createTime
                    holder.dataBinding!!.itemTeamUserAchievementLay.visibility = View.GONE
                    holder.dataBinding!!.itemTeamUserAgentNum.text = ""
                    holder.dataBinding!!.itemTeamUserSettleMonth.text = ""
                    holder.dataBinding!!.itemTeamUserSettleYesterday.text = ""
                    holder.dataBinding!!.itemTeamUserAchievementMonth.text = ""
                    holder.dataBinding!!.itemTeamUserPhoneIv.setOnClickListener(object :NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            if (StringUtil.isEmpty(item.phone)) {
                                ToastUtils.show("手机号不存在！")
                                return
                            }
                            val cm =
                                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            // 将文本内容放到系统剪贴板里。
                            cm.text = item.phone
                            ToastUtils.show("手机号已复制")
                        }

                        override fun OnMoreErrorClick() {
                        }

                    })
                    holder.dataBinding!!.itemTeamUserWechatIv.setOnClickListener(object :NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            if (StringUtil.isEmpty(item.wxnum)){
                                ToastUtils.show("微信号不存在！")
                                return
                            }

                            val cm =
                                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            // 将文本内容放到系统剪贴板里。
                            cm.text = item.wxnum
                            ToastUtils.show("微信号已复制")
                        }

                        override fun OnMoreErrorClick() {
                        }

                    })

                    holder.dataBinding!!.itemTeamUserAchievement.setOnClickListener(object :NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            if (holder.dataBinding!!.itemTeamUserAchievementLay.visibility == View.VISIBLE){
                                holder.dataBinding!!.itemTeamUserAchievementLay.visibility = View.GONE
                            } else{
                                //请求团队业绩接口
                                val teamAchiDataResult = MutableLiveData<ResultState<TeamAchiDataBean>>()
                                teamAchiDataResult.observe(this@TeamDetailActivity){ it->
                                    parseState(it,{
                                        holder.dataBinding!!.itemTeamUserAgentNum.text = it.count.toString()
                                        holder.dataBinding!!.itemTeamUserSettleMonth.text = it.tmSettleCount.toString()
                                        holder.dataBinding!!.itemTeamUserSettleYesterday.text = it.ydTeamSettleCount.toString()
                                        holder.dataBinding!!.itemTeamUserAchievementMonth.text = AppUtil.getStringAmount(it.tmTeamSettleAmt)
                                        holder.dataBinding!!.itemTeamUserAchievementLay.visibility = View.VISIBLE

                                    },{
                                        LogUtil.e("TeamDetailActivity---teamAchiDataResult---"+it.errorLog,it.errorMsg)
                                        ToastUtils.show("获取失败，稍后重试！")
                                    })
                                }
                                mViewModel.getTeamAchv(item.id,teamAchiDataResult)


                            }
                        }

                        override fun OnMoreErrorClick() {
                        }

                    })

                }

            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(this@TeamDetailActivity).inflate(R.layout.empty_view_tab, null))
        mBinding.teamDetailListRecycler.adapter = mViewModel.adapter
    }

}