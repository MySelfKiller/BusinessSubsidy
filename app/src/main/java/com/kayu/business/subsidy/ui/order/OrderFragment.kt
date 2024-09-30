package com.kayu.business.subsidy.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.flyco.tablayout.TabEntity
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.MainActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.ItemOrderBean
import com.kayu.business.subsidy.databinding.FragmentOrderBinding
import com.kayu.business.subsidy.databinding.ItemOrderLayBinding
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding, OrderViewModel>() {
    var title = "订单详情"
    var back = "返回"
    override val mViewModel: OrderViewModel by viewModels()

    companion object {
        fun newInstance() = OrderFragment()
    }

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }
    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !mViewModel.mHasLoadedOnce) {
//            mBinding.orderRefreshLayout.autoRefresh()
            WaitDialog.show(
                requireActivity() as AppCompatActivity,
                "加载中..."
            )
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter!!.notifyDataSetChanged()
            }
            reqListData(mViewModel.pageIndex)
            mViewModel.mHasLoadedOnce = true
        }
    }

    override fun initObserve() {
        mViewModel.deleteOrderResult.observe(this){ it->
            parseState(it,{
//                mBinding.orderRefreshLayout.autoRefresh()
                WaitDialog.show(
                    requireActivity() as AppCompatActivity,
                    "加载中..."
                )
                mViewModel.isRefresh = true
                mViewModel.pageIndex = 1
                if (null != mViewModel.adapter) {
                    mViewModel.adapter!!.data.clear()
                    mViewModel.adapter!!.notifyDataSetChanged()
                }
                reqListData(mViewModel.pageIndex)
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
        mViewModel.orderListResult.observe(this){ it ->
            parseState(it,{
                val listNewData = mutableListOf<ItemOrderBean>()
                it.list?.let { it1 -> listNewData.addAll(it1) }
                if (null == mViewModel.orderListData.value ) {
                    mViewModel.orderListData.value = mutableListOf()
                } else {
                    if (mViewModel.isRefresh) {
                        mViewModel.orderListData.value?.clear()
                    }
                }
                mViewModel.orderListData.value?.addAll(listNewData)
                loadListData(mViewModel.orderListData.value as MutableList<ItemOrderBean>)
            },{
                LogUtil.e("mySettlementListResult",it.errorMsg)
            })
            if (mViewModel.isRefresh) {
                mBinding.orderRefreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            if (mViewModel.isLoadMore) {
                mBinding.orderRefreshLayout.finishLoadMore()
                mViewModel.isLoadMore = false
            }
            WaitDialog.dismiss()
        }
    }

    private fun loadListData(list:MutableList<ItemOrderBean>){
        if (null != mViewModel.adapter) {
            mViewModel.adapter?.data?.clear()
            mViewModel.adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<ItemOrderBean>){
        val newList = mutableListOf<ItemOrderBean>()
        newList.addAll(list)
        mViewModel.adapter = object : BaseQuickAdapter<ItemOrderBean, BaseDataBindingHolder<ItemOrderLayBinding>>
            (R.layout.item_order_lay, newList) {
            @SuppressLint("Range")
            override fun convert(
                holder: BaseDataBindingHolder<ItemOrderLayBinding>,
                item: ItemOrderBean
            ) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemOrderUserName.text = item.name
                    holder.dataBinding!!.itemOrderTime.text = item.createTime
                    holder.dataBinding!!.itemOrderProductLogo.load(item.productLogo){
                        this.allowHardware(false)
                    }
                    holder.dataBinding!!.itemOrderProduct.text = item.productName
//                    if (StringUtil.isEmpty(item.tips)) {
//                        holder.dataBinding!!.itemOrderStateTip.visibility = View.GONE
//                    } else {
//                        holder.dataBinding!!.itemOrderStateTip.visibility = View.VISIBLE
//                        holder.dataBinding!!.itemOrderStateTip.text = item.tips
//                    }
                    holder.dataBinding!!.itemOrderDetail.setOnClickListener(object :NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            val intent = Intent(requireContext(),OrderDetailActivity::class.java)
                            intent.putExtra("orderID",item.id)
                            intent.putExtra("title", "订单详情")
                            intent.putExtra("back", "我的")
                            startActivity(intent)
                        }

                        override fun OnMoreErrorClick() {
                        }
                    })

                    holder.dataBinding!!.itemOrderPhone.text =  item.phone
                    holder.dataBinding!!.itemOrderUserName.text = item.name
                    holder.dataBinding!!.itemCurrentState.text = item.userApplyStatusTitle
                    holder.dataBinding!!.itemNewUser.text =  item.isNewUserTitle
                    if (item.state == 1 ) {
                        holder.dataBinding!!.itemOrderDelete.visibility = View.VISIBLE
                        holder.dataBinding!!.itemOrderDelete.setOnClickListener(object : NoMoreClickListener() {
                            override fun OnMoreClick(view: View) {
                                mViewModel.deleteOrder(item.id)
                            }

                            override fun OnMoreErrorClick() {}
                        })
                    } else {
                        holder.dataBinding!!.itemOrderDelete.visibility = View.GONE
                    }
                    holder.dataBinding!!.itemOrderState.text = item.tag
//                    holder.dataBinding!!.itemOrderState.setTextColor()

//                    val gd = holder.dataBinding!!.itemOrderState.background as GradientDrawable
//                    gd.setColor(Color.parseColor(item.color))
//                    holder.dataBinding!!.itemOrderState.background = gd
                }
            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(requireContext()).inflate(R.layout.empty_view_tab, null))
        mBinding.orderRecyclerView.adapter = mViewModel.adapter
    }


    override fun initRequestData() {
    }

    override fun FragmentOrderBinding.initView() {
        if (activity?.javaClass?.equals(MainActivity::class.java) == true){
            mBinding.fragTitleRootLay.visibility = View.VISIBLE
        }else{
            mBinding.fragTitleRootLay.visibility = View.GONE
        }
        val mTabEntities = ArrayList<CustomTabEntity>()
        mTabEntities.add(TabEntity("全部", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mTabEntities.add(TabEntity("待确认", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mTabEntities.add(TabEntity("已结算", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mTabEntities.add(TabEntity("已失效", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mBinding.orderTabLayout.setTabData(mTabEntities)
        mBinding.orderTabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mViewModel.state = position
                mViewModel.pageIndex = 1
                mViewModel.isRefresh = true
                if (null != mViewModel.adapter) {
                    mViewModel.adapter!!.data.clear()
                    mViewModel.adapter?.notifyDataSetChanged()
                }
                reqListData(mViewModel.pageIndex)
            }

            override fun onTabReselect(position: Int) {}
        })


        mBinding.orderRefreshLayout.setEnableAutoLoadMore(false)
        mBinding.orderRefreshLayout.setEnableLoadMore(true)
        mBinding.orderRefreshLayout.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能

        mBinding.orderRefreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹

        mBinding.orderRefreshLayout.setEnableOverScrollDrag(true)
        mBinding.orderRefreshLayout.setOnRefreshListener(OnRefreshListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnRefreshListener
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter?.notifyDataSetChanged()
            }
            reqListData(mViewModel.pageIndex)
        })
        mBinding.orderRefreshLayout.setOnLoadMoreListener(OnLoadMoreListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnLoadMoreListener
            mViewModel.isLoadMore = true
            mViewModel.pageIndex += 1
            reqListData(mViewModel.pageIndex)
        })
        mBinding.orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        mBinding.orderSearchDelete.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                mViewModel.searchKey = ""
                mBinding.orderSearchEdt.setText("")
                mBinding.orderSearchDelete.visibility = View.INVISIBLE
                val imm = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
                imm.hideSoftInputFromWindow(view.windowToken, 0) //强制隐藏键盘
//                mBinding.orderRefreshLayout.autoRefresh()
                WaitDialog.show(
                    requireActivity() as AppCompatActivity,
                    "加载中..."
                )
                mViewModel.isRefresh = true
                mViewModel.pageIndex = 1
                if (null != mViewModel.adapter) {
                    mViewModel.adapter!!.data.clear()
                    mViewModel.adapter!!.notifyDataSetChanged()
                }
                reqListData(mViewModel.pageIndex)
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.idTagOrderSearch.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                mBinding.orderSearchEdt.isFocusable = true
                mBinding.orderSearchEdt.isFocusableInTouchMode = true
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.orderSearchEdt.inputType = InputType.TYPE_CLASS_TEXT
        mBinding.orderSearchEdt.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mViewModel.searchKey = mBinding.orderSearchEdt.text.toString()
                if (StringUtil.isEmpty(mViewModel.searchKey)) {
                    ToastUtils.show("请输入客户姓名/手机号搜索订单")
                } else {
                    val inputMethodManager =
                        requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isActive) {
                        inputMethodManager.hideSoftInputFromWindow(
                            mBinding.orderSearchEdt.windowToken,
                            0
                        )
//                        mBinding.orderRefreshLayout.autoRefresh()
                        WaitDialog.show(
                            requireActivity() as AppCompatActivity,
                            "加载中..."
                        )
                        mViewModel.isRefresh = true
                        mViewModel.pageIndex = 1
                        if (null != mViewModel.adapter) {
                            mViewModel.adapter!!.data.clear()
                            mViewModel.adapter!!.notifyDataSetChanged()
                        }
                        reqListData(mViewModel.pageIndex)
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }
        mBinding.orderSearchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (StringUtil.isEmpty(s.toString().trim { it <= ' ' })) {
                    mViewModel.searchKey = ""
                    mBinding.orderSearchDelete.visibility = View.INVISIBLE
                } else {
                    mBinding.orderSearchDelete.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun reqListData(page: Int){
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = page
        if (mViewModel.state!= 0){
            reqDateMap["state"] = mViewModel.state
        }
        reqDateMap["pageSize"] = 10
        if (!StringUtil.isEmpty(mViewModel.searchKey)){
            reqDateMap["keyword"] = mViewModel.searchKey
        }
        //"startTime":"2022-11-01 00:00:00",
        //"endTime":"2022-11-20 02:00:00"
//        if (!StringUtil.isEmpty(startTime) && !StringUtil.isEmpty(endTime)){
//            reqDateMap["startTime"] = startTime
//            reqDateMap["endTime"] = endTime
//        }
        mViewModel.getOrderList(reqDateMap)

    }
}