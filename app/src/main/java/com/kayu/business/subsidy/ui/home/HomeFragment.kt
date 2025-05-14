package com.kayu.business.subsidy.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import coil.load
import com.amap.api.location.AMapLocation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.NoticeBean
import com.kayu.business.subsidy.data.bean.ProductItemBean
import com.kayu.business.subsidy.databinding.FragmentHomeBinding
import com.kayu.business.subsidy.databinding.ItemHomeCreditCardBinding
import com.kayu.business.subsidy.ui.WebViewActivity
import com.kayu.business.subsidy.ui.notice.NoticeActivity
import com.kayu.business.subsidy.ui.team.TeamFragment
import com.kayu.utils.AppUtil.getStringIntAmount
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.location.LocationCallback
import com.kayu.utils.location.LocationManagerUtil
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.WaitDialog
import com.youth.banner.BannerConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    var IsClick =false

    override val mViewModel: HomeViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

//    fun setNaviGridView(navPopups: MutableList<ItemActivityBean>?) {
//        if (null != mBinding.homeNaviRecycler && null != navPopups && navPopups.isNotEmpty()) {
//            mBinding.homeNaviRecycler.layoutManager = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
//            mBinding.homeNaviRecycler.adapter = object : BaseQuickAdapter<ItemActivityBean,
//                    BaseDataBindingHolder<ItemHomeNaviLayBinding>>(R.layout.item_home_navi_lay, navPopups) {
//                override fun convert(
//                    holder: BaseDataBindingHolder<ItemHomeNaviLayBinding>,
//                    item: ItemActivityBean
//                ) {
//                    if (null != holder.dataBinding) {
//                        holder.dataBinding!!.itemGridName.text = item.title
//                        holder.dataBinding!!.itemGridImg.load(item.pic)
//                        holder.dataBinding!!.itemGridLay.setOnClickListener(object : NoMoreClickListener() {
//                            override fun OnMoreClick(view: View) {
//                                val target: String = item.url
//                                if (!isEmpty(target)) {
//                                    val intent = Intent(context, WebViewActivity::class.java)
//                                    val sb = StringBuilder()
//                                    sb.append(target)
//                                    if (target.contains("?")) {
//                                        sb.append("&token=")
//                                    } else {
//                                        sb.append("?token=")
//                                    }
//                                    sb.append(SMApplication.instance.token)
//                                    intent.putExtra("url", sb.toString())
//                                    intent.putExtra("from", "首页")
//                                    context.startActivity(intent)
//                                } else {
//                                    ToastUtils.show("链接不存在！")
//                                }
//                            }
//
//                            override fun OnMoreErrorClick() {}
//                        })
//                    }
//                }
//
//            }
//        }
//    }

    override fun FragmentHomeBinding.initView() {
//        mBinding.homeMsgDetail.setOnClickListener(object : NoMoreClickListener(){
//            override fun OnMoreClick(view: View) {
//                startActivity(Intent(requireContext(),NoticeActivity::class.java))
//            }
//
//            override fun OnMoreErrorClick() {
//            }
//
//        })

        mBinding.homeProductRecycleView.layoutManager =
            GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
        mBinding.homeRefreshLayout.setEnableAutoLoadMore(false)
        mBinding.homeRefreshLayout.setEnableLoadMore(false)
        mBinding.homeRefreshLayout.setEnableLoadMoreWhenContentNotFull(false) //是否在列表不满一页时候开启上拉加载功能
        mBinding.homeRefreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹
        mBinding.homeRefreshLayout.setEnableOverScrollDrag(true)
        mBinding.homeRefreshLayout.setOnRefreshListener {
            if (mViewModel.isRefresh) return@setOnRefreshListener
            mViewModel.isRefresh = true
            reqData()
        }
//        mBinding.homeRefreshLayout.autoRefresh()
        WaitDialog.show(
            requireActivity() as AppCompatActivity,
            "加载中..."
        )
        mViewModel.isRefresh = true
        reqData()
    }

    override fun initObserve() {
        mViewModel.noticeListData.observe(this) { it ->
            parseState(it, {
                if (!it.list.isNullOrEmpty()) {
                    var strList = mutableListOf<String>()
                    var urlList = mutableListOf<NoticeBean>()
                    for (item in it.list!!){
                        strList.add(item.msg)
                        urlList.add(item)
                    }
                    mBinding.homeHostTextBanner.setDatas(strList)
                    mBinding.homeHostTextBanner.setItemOnClickListener { data, position ->
                        if ( urlList[position].messageId>0){
                            if (!urlList[position].url.isNullOrEmpty() ){
                                judgeURL2Jump(urlList[position].url)
                            }
                        } else{
                            startActivity(Intent(requireContext(),NoticeActivity::class.java))
                        }
                    }
                }
            }, {
                LogUtil.e("noticeListData", it.toString())
            })
        }
//        mViewModel.hotProductListLiveData.observe(this){ it ->
//            parseState(it,{
////                mBinding.homeProductHotTitle.text = it.title
//                mBinding.homeProductHotSubTitle.text = it.tips
//                if (it.productList.isNullOrEmpty()){
//                    mBinding.idHomeProductHotLay.visibility = View.GONE
//                }else{
//                    for (x in it.productList.indices) {
//                        if (x==0){
//                            val hotProduct = it.productList[x]
//                            mBinding.homeHotPro1Icon.load(hotProduct.logo){
//                                this.allowHardware(false)
//                            }
//                            mBinding.homeHotPro1Name.text = hotProduct.name
//                            mBinding.homeHotPro1Tip.text = hotProduct.settlementCycle
//                            if (hotProduct.extraAmount > 0) {
//                                mBinding.homeHotPro1Price.text =
//                                    getStringIntAmount(hotProduct.rewardAmount) + "+" + getStringIntAmount(hotProduct.extraAmount)
//
//                            } else {
//                                mBinding.homeHotPro1Price.text =
//                                    getStringIntAmount(hotProduct.rewardAmount)
//                            }
//                            mBinding.idHomeProductHotLay.visibility = View.VISIBLE
//                            mBinding.homeHotPro1Lay.setOnClickListener(object :NoMoreClickListener(){
//                                override fun OnMoreClick(view: View) {
//                                    val intent = Intent(context, CardDetailActivity::class.java)
//                                    intent.putExtra("title", hotProduct.name)
//                                    intent.putExtra("back", "首页")
//                                    SMApplication.instance.itemData = hotProduct
//                                    startActivity(intent)
//                                }
//
//                                override fun OnMoreErrorClick() {
//                                }
//
//                            })
//
//                            mBinding.idHomeProductHotLay.visibility = View.VISIBLE
//                        }
//
//                        if (x==1){
//                            mBinding.homeHotPro2Lay.visibility=View.VISIBLE
//                            val hotProduct2 = it.productList[x]
//                            mBinding.homeHotPro2Icon.load(hotProduct2.logo){
//                                this.allowHardware(false)
//                            }
//                            mBinding.homeHotPro2Name.text = hotProduct2.name
//                            mBinding.homeHotPro2Tip.text = hotProduct2.settlementCycle
//                            if (hotProduct2.extraAmount > 0) {
//                                mBinding.homeHotPro2Price.text =
//                                    getStringIntAmount(hotProduct2.rewardAmount) + "+" + getStringIntAmount(hotProduct2.extraAmount)
//
//                            } else {
//                                mBinding.homeHotPro2Price.text =
//                                    getStringIntAmount(hotProduct2.rewardAmount)
//                            }
//                            mBinding.homeHotPro2Lay.setOnClickListener(object :NoMoreClickListener(){
//                                override fun OnMoreClick(view: View) {
//                                    val intent = Intent(context, CardDetailActivity::class.java)
//                                    intent.putExtra("title", hotProduct2.name)
//                                    intent.putExtra("back", "首页")
//                                    SMApplication.instance.itemData = hotProduct2
//                                    startActivity(intent)
//                                }
//
//                                override fun OnMoreErrorClick() {
//                                }
//
//                            })
//                        }
//
//                    }
//
//                }
//            },{
//                mBinding.idHomeProductHotLay.visibility = View.GONE
//            })
//        }
        mViewModel.productGroupListLiveData.observe(this){ it->
            parseState(it,{
                val mTabEntities = mutableListOf<String>()
                val mTabIDs = mutableListOf<Long>()
                val fragments = mutableListOf<Fragment>()
//                mTabEntities.add(TabEntity("全部", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
                mTabEntities.add("全部")
                mTabIDs.add(0)
                fragments.add(TeamFragment())
                for (item in it){
//                    mTabEntities.add(TabEntity(item.name, R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
                    mTabEntities.add(item.name)
                    fragments.add(TeamFragment())
                    mTabIDs.add(item.id)
                }


//                mBinding.homeTabLayout.setTabData(mTabEntities)
                mBinding.homeVp.adapter = MyPagerAdapter(childFragmentManager, fragments,mTabEntities.toTypedArray())
                mBinding.homeTabLayout.setViewPager(mBinding.homeVp,
                    mTabEntities.toTypedArray()
                );
                mBinding.homeTabLayout.setOnTabSelectListener(object: OnTabSelectListener{
                    override fun onTabSelect(position: Int) {
                        IsClick = true
                        if (!mViewModel.isRefresh){
                            if (position == 0 ){
                                mViewModel.getProductListData(cityName)
                            } else{
                                mViewModel.getProductListData(mTabIDs[position],cityName)
                            }
                        }

                    }

                    override fun onTabReselect(position: Int) {
                    }
                })
            },{

            })
        }
        mViewModel.productListLiveData.observe(this) { it ->
            parseState(it, {
                if (null != mViewModel.adapter) {
                    mViewModel.adapter?.data?.clear()
                    mViewModel.adapter?.addData(it)
                } else {
                    createAdapter(it)
                }
            }, {
                LogUtil.e("产品数据报错", it.toString())
            })

            if (mViewModel.isRefresh) {
                if (IsClick){
                    mBinding.homeTabLayout.currentTab = 0
                }
                mBinding.homeRefreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            WaitDialog.dismiss()
        }

        mViewModel.bannerListLiveData.observe(this){ it ->
            parseState(it,{bannerBeans->
                val urlList: MutableList<String?> = ArrayList()
                for (item in bannerBeans) {
                    urlList.add(item.img)
                }
                mBinding.homeSmartBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                mBinding.homeSmartBanner.setIndicatorGravity(BannerConfig.RIGHT)
                mBinding.homeSmartBanner.setImageLoader(BannerImageLoader())
                mBinding.homeSmartBanner.setImages(urlList)
                mBinding.homeSmartBanner.setDelayTime(2000)
                mBinding.homeSmartBanner.setOnBannerListener { position ->
                    val target = bannerBeans[position].url
                    judgeURL2Jump(target)
                }
                mBinding.homeSmartBanner.start()
                    .setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) { }

                        override fun onPageSelected(position: Int) { }

                        override fun onPageScrollStateChanged(state: Int) { }
                    })

            },{
                LogUtil.e("产品数据报错", it.toString())
            })
        }
    }

    private fun createAdapter(list: MutableList<ProductItemBean>) {
        val newList = mutableListOf<ProductItemBean>()
        newList.addAll(list)
        mViewModel.adapter = object :
            BaseQuickAdapter<ProductItemBean, BaseDataBindingHolder<ItemHomeCreditCardBinding>>(
                R.layout.item_home_credit_card,
                newList
            ) {

            @SuppressLint("SetTextI18n")
            override fun convert(
                holder: BaseDataBindingHolder<ItemHomeCreditCardBinding>,
                item: ProductItemBean
            ) {
                if (null != holder.dataBinding) {
                    holder.dataBinding!!.itemProName.text = item.name
                    holder.dataBinding!!.itemSettleCycle.text = item.settlementCycle
                    holder.dataBinding!!.itemProIcon.load(item.logo) {
                        this.allowHardware(false)
                        //                                createBitmap(layoutParams.width, layoutParams.height, Bitmap.Config.ARGB_8888)
                    }
                    if (item.extraAmount > 0) {
                        holder.dataBinding!!.itemProCommission.text =
                            getStringIntAmount(item.rewardAmount) + "+" + getStringIntAmount(item.extraAmount)

                    } else {
                        holder.dataBinding!!.itemProCommission.text =
                            getStringIntAmount(item.rewardAmount)
                    }

                    holder.dataBinding!!.itemProductLay.setOnClickListener(object :
                        NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            val intent = Intent(context, CardDetailActivity::class.java)
                            intent.putExtra("title", item.name)
                            intent.putExtra("back", "首页")
                            SMApplication.instance.itemData = item
                            startActivity(intent)
                        }

                        override fun OnMoreErrorClick() {}
                    })

                }

            }
        }
        mViewModel.adapter?.setEmptyView(
            LayoutInflater.from(requireContext()).inflate(R.layout.empty_view_tab, null)
        )
        mBinding.homeProductRecycleView.adapter = mViewModel.adapter
    }


    override fun initRequestData() {
        mViewModel.getBannerListData()
        mViewModel.getProductGroupListData()
    }

    private var latitude = 0.0
    private var longitude = 0.0
    private var cityName: String = "定位失败"

    private fun reqData() {
        val reqDateMap = HashMap<String, Any?>()
        reqDateMap["pageNow"] = 1
        mViewModel.getNoticeList(reqDateMap)
        SMApplication.instance.checkPermission(requireActivity(), arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        ), {
            if (LocationManagerUtil.self?.isLocServiceEnable == true){
                LocationManagerUtil.self?.reStartLocation()
                LocationManagerUtil.self?.setLocationListener(object : LocationCallback {
                    override fun onLocationChanged(location: AMapLocation?) {
                        if (null != location) {
                            latitude = location.latitude
                            longitude = location.longitude
                            cityName = location.city
                            mViewModel.getProductListData(cityName)
//                            mViewModel.getHotProductListData(cityName)
                        }else{
                            mViewModel.getProductListData("")
//                            mViewModel.getHotProductListData("")
                        }
                        mBinding.homeLocationCity.text = cityName
                    }
                })
            }else{
                if (mViewModel.isRefresh) {
                    mBinding.homeRefreshLayout.finishRefresh()
                    mViewModel.isRefresh = false
                }
                ToastUtils.show("请开启定位服务！")
            }

        }, {
//            mViewModel.getHotProductListData("")
            mViewModel.getProductListData("")
            mBinding.homeLocationCity.text = cityName
        })

    }

    /**
     *验证url并调整相应的activity
     */
    private fun judgeURL2Jump(url:String?) {
        if (!url.isNullOrEmpty()) {
            val intent = Intent(context, WebViewActivity::class.java)
            val sb = StringBuilder()
            sb.append(url)
            if (url.contains("?")) {
                sb.append("&token=")
            } else {
                sb.append("?token=")
            }
            sb.append(SMApplication.instance.token)
            sb.append("&locationName=")
            sb.append(cityName)
            sb.append("&selectLocation=")
            sb.append(longitude)
            sb.append(",")
            sb.append(latitude)
            intent.putExtra("url", sb.toString())
            intent.putExtra("from", "首页")
            startActivity(intent)
        } else {
            MessageDialog.show(
                (requireContext() as AppCompatActivity),
                "温馨提示",
                "功能未开启，敬请期待"
            )
        }
    }
    private class MyPagerAdapter(fm: FragmentManager?, val fragments: MutableList<Fragment>, val mTitles:Array<String>) :
        FragmentPagerAdapter(fm!!) {
        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }
        override fun getItem(position: Int): Fragment {
            return fragments.get(position)
        }
    }
}