package com.kayu.business.subsidy

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build.VERSION.SDK_INT
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.kayu.business.subsidy.data.bean.ItemActivityBean
import com.kayu.business.subsidy.databinding.ActivityMainBinding
import com.kayu.business.subsidy.ui.WebViewActivity
import com.kayu.business.subsidy.ui.home.HomeFragment
import com.kayu.business.subsidy.ui.mine.MineFragment
import com.kayu.business.subsidy.ui.notice.NoticeFragment
import com.kayu.business.subsidy.ui.order.OrderFragment
import com.kayu.business.subsidy.ui.settlement.MyPagerAdapter
import com.kayu.business.subsidy.ui.team.PerformanceFragment
import com.kayu.business.subsidy.utils.getImageBitmapByUrl
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil.isEmpty
import com.kongzue.dialog.v3.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    var startActivityLaunch: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == 111) { //连续活动弹窗
            if (showActivityIndex < dialogActivityList!!.size - 1) {
                showActivityIndex++
                dialogShow(showActivityIndex)
            }
        }
    }
    override val mViewModel: MainViewModel by viewModels()

    private var lastSelectItemid = 0
    private val mFragments = java.util.ArrayList<Fragment>()

    //    public ArrayList<ActiveDialogData> navPopups;//首页展示列表
//    private val mOnNavigationItemSelectedListener =
//        BottomNavigationView.OnNavigationItemSelectedListener { item -> //            Log.e("hm","NavigationItemSelected position="+item.getItemId());
//            if (lastSelectItemid == item.itemId) {
//                return@OnNavigationItemSelectedListener true
//            }
//            lastSelectItemid = item.itemId
//            when (item.itemId) {
//                R.id.navigation_home -> mBinding.mainViewPager.setCurrentItem(0,false)
//                R.id.navigation_order -> mBinding.mainViewPager.setCurrentItem(1,false)
//                R.id.navigation_notice -> {
//                    mBinding.mainViewPager.setCurrentItem(2,false)
//                    mBinding.navView.getOrCreateBadge(R.id.navigation_notice).isVisible = false
//                }
//                R.id.navigation_performance -> mBinding.mainViewPager.setCurrentItem(3,false)
//                R.id.navigation_mine -> mBinding.mainViewPager.setCurrentItem(4,false)
//            }
//            true
//        }
    var badge: Badge? = null
    override fun ActivityMainBinding.initView() {
//        val navView = mBinding.navView
//        navView.getOrCreateBadge(R.id.navigation_notice).backgroundColor = resources.getColor(R.color.red,null)
//        navView.itemIconTintList = null //设置item图标颜色为null，当menu里icon设置selector的时候，
        val states = arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_activated),
            intArrayOf()
        )

        val colors = intArrayOf(
            resources.getColor(R.color.transparent,null),
            resources.getColor(R.color.transparent,null),
            resources.getColor(R.color.transparent,null),
            resources.getColor(R.color.transparent,null)
        )
        val colorStateList = ColorStateList(states, colors)

        //初始化 bottomBar
        bottomView.init{
            when (it) {
                R.id.navigation_home -> {
                    mBinding.mainViewPager.setCurrentItem(0,false)
                    mBinding.littleActivity.visibility = View.VISIBLE
                    mBinding.littleActivity2.visibility = View.VISIBLE
                    mBinding.littleActivity3.visibility = View.VISIBLE
                }
                R.id.navigation_order -> {
                    mBinding.mainViewPager.setCurrentItem(1,false)
                    mBinding.littleActivity.visibility = View.GONE
                    mBinding.littleActivity2.visibility = View.GONE
                    mBinding.littleActivity3.visibility = View.GONE
                }
                R.id.navigation_notice -> {
                    mBinding.mainViewPager.setCurrentItem(2,false)
                    badge?.hide(true)
                    mBinding.littleActivity.visibility = View.GONE
                    mBinding.littleActivity2.visibility = View.GONE
                    mBinding.littleActivity3.visibility = View.GONE
//                    mBinding.bottomView.getOrCreateBadge(R.id.navigation_notice).isVisible = false
                }
                R.id.navigation_performance -> {
                    mBinding.mainViewPager.setCurrentItem(3,false)
                    mBinding.littleActivity.visibility = View.GONE
                    mBinding.littleActivity2.visibility = View.GONE
                    mBinding.littleActivity3.visibility = View.GONE
                }
                R.id.navigation_mine -> {
                    mBinding.mainViewPager.setCurrentItem(4,false)
                    mBinding.littleActivity.visibility = View.GONE
                    mBinding.littleActivity2.visibility = View.GONE
                    mBinding.littleActivity3.visibility = View.GONE
                }
            }
        }
        bottomView.interceptLongClick(R.id.navigation_home,R.id.navigation_order,R.id.navigation_notice,R.id.navigation_performance,R.id.navigation_mine)


//        navView.itemActiveIndicatorColor = colorStateList
//        BottomNavigationViewHelper.disableShiftMode(navView)
        //        ((mBinding.navView.getChildAt(0) as BottomNavigationMenuView).getChildAt(2) as BottomNavigationItemView).let {
//            it.badge.apply {
//
//                backgroundColor = ContextCompat.getColor( this@BadgeDrawableActivity, R.color.red)
//
//                badgeTextColor = ContextCompat.getColor( this@BadgeDrawableActivity, R.color.white)
//
//            }
//
//        }

//        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        mFragments.add(HomeFragment())
        mFragments.add(OrderFragment.newInstance())
        mFragments.add(NoticeFragment.newInstance())
        mFragments.add(PerformanceFragment())
        mFragments.add(MineFragment())
        val adapter = MyPagerAdapter(supportFragmentManager, mFragments,lifecycle)
        mBinding.mainViewPager.offscreenPageLimit = 5
        mBinding.mainViewPager.isUserInputEnabled = false
        mBinding.mainViewPager.adapter = adapter
//        mBinding.mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//
//                var selectedItemId = 0
//                when (position) {
//                    0 -> selectedItemId = R.id.navigation_home
//                    1 -> selectedItemId = R.id.navigation_order
//                    2 -> {
//                        selectedItemId = R.id.navigation_notice
//                        mBinding.navView.getOrCreateBadge(R.id.navigation_notice).isVisible = false
//                    }
//                    3 -> selectedItemId = R.id.navigation_performance
//                    4 -> selectedItemId = R.id.navigation_mine
//                }
//                Log.e("hm", "viewPage getSelectedItemId=" + navView.selectedItemId)
//                Log.e("hm", "viewPage selectedItemId=$selectedItemId")
//                if (navView.getSelectedItemId() != selectedItemId) {
//                    navView.setSelectedItemId(selectedItemId)
//                }
//            }
//        })
        mBinding.mainViewPager.currentItem = 0
//        navView.selectedItemId = 0
    }

    private fun addBadgeAt(position: Int, number: Int): Badge? {
        return QBadgeView(this)
            .setBadgeNumber(number)
            .setBadgeTextColor(resources.getColor(R.color.red,null))
            .setBadgeTextSize(6f,true)
            .setBadgeBackgroundColor(resources.getColor(R.color.red,null))
            .setBadgePadding(0f,true)
            .setGravityOffset(18f, 8f, true)
            .bindTarget(mBinding.bottomView.getBottomNavigationItemView(position))
            .setOnDragStateChangedListener { dragState, badge, targetView ->
//                if (OnDragStateChangedListener.STATE_SUCCEED == dragState) ToastUtils.show("移除红点")
            }
        // add badge
    }

    override fun initObserve() {
        mViewModel.msgUnReadResult.observe(this){ it->
            parseState(it,{
                badge = addBadgeAt(2,1)
                if (it as Double >0) {
                }else{
                    badge?.hide(true)
                }
            },{
                LogUtil.e("msgUnReadResult",it.errorMsg)
            })
        }
        mViewModel.activityListResult.observe(this){ it->
            parseState(it,{
                //测试数据
//        SystemParam ss = systemParamList.get(0);
//        systemParamList.add(ss);
//        systemParamList.add(ss);
//        systemParamList.add(ss);
                // TODO: 添加获取导航图标
//                val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_fragment_main) as NavHostFragment
//                ( navHostFragment.childFragmentManager.fragments[0] as HomeFragment).setNaviGridView(it.navPopups)
                showPopuView(it.tipPopups)

                showLittleActivity(it.fixedPopups)

            },{
                LogUtil.e("activityListResult",it.errorMsg)
            })
        }

    }

    override fun initRequestData() {
        mViewModel.getActivityList()
        mViewModel.getMsgUnreadNum()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showLittleActivity(dialogDataArrayList: ArrayList<ItemActivityBean>?) {
        if (null == dialogDataArrayList || dialogDataArrayList.isEmpty()) {
            return
        }
        val imageLoader = ImageLoader.Builder(this@MainActivity)
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(this@MainActivity))
                } else {
                    add(GifDecoder())
                }
            }.build()
        for (x in dialogDataArrayList.indices) {
            if (x > 2) return

            val jumpUrl: String = dialogDataArrayList[x].url
            val imgUrl: String = dialogDataArrayList[x].pic
            if (isEmpty(imgUrl)) return
            if (x==0){
                val littleActLogo: ImageView = mBinding.littleActivity
                LogUtil.e("hm","start show littleActivity")
                seLittleLogo(littleActLogo,jumpUrl,imgUrl,imageLoader)
                LogUtil.e("hm","end show littleActivity")

            }
            if (x == 1) {
                val littleActLogo: ImageView = mBinding.littleActivity2
                LogUtil.e("hm","start show littleActivity2")
                seLittleLogo(littleActLogo,jumpUrl,imgUrl,imageLoader)
                LogUtil.e("hm","end show littleActivity2")
            }
            if (x==2){
                val littleActLogo: ImageView = mBinding.littleActivity3
                LogUtil.e("hm","start show littleActivity3")
                seLittleLogo(littleActLogo,jumpUrl,imgUrl,imageLoader)
                LogUtil.e("hm","end show littleActivity3")
            }
            //创建 gif ImageLoader 实例

        }
    }

    private fun seLittleLogo(littleActLogo:ImageView,jumpUrl:String,imgUrl:String,imageLoader: ImageLoader){

        littleActLogo.load(imgUrl,imageLoader){
            this.allowHardware(false)
        }
        littleActLogo.visibility = View.VISIBLE
        littleActLogo.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (!isEmpty(jumpUrl)) {
                    val intent = Intent(this@MainActivity, WebViewActivity::class.java)
                    val sb = StringBuilder()
                    sb.append(jumpUrl)
                    if (jumpUrl.contains("?")) {
                        sb.append("&token=")
                    } else {
                        sb.append("?token=")
                    }
                    sb.append(SMApplication.instance.token)
                    intent.putExtra("url", sb.toString())
                    intent.putExtra("from", "首页")
                    startActivity(intent)
                } else {
                    ToastUtils.show("链接不存在！")
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    var dialogActivityList: ArrayList<ItemActivityBean>? = null
    var showActivityIndex = 0
    private fun showPopuView(dialogActivityList: ArrayList<ItemActivityBean>?) {
        if (null == dialogActivityList || dialogActivityList.isEmpty()) {
            return
        }
        this.dialogActivityList = dialogActivityList
        dialogShow(showActivityIndex)
    }

    private fun dialogShow(index: Int) {
        val jumpUrl: String = dialogActivityList!![index].url
        val imgUrl: String = dialogActivityList!![index].pic
        if (isEmpty(imgUrl)) return

        mViewModel.viewModelScope.launch {
            val resource = getImageBitmapByUrl(imgUrl) ?: return@launch
            withContext(Dispatchers.Main) {
                CustomDialog.show(this@MainActivity, R.layout.dialog_activity) { dialog, v ->
                    val showAcy: ImageView = v.findViewById(R.id.act_show_img)
                    showAcy.setImageBitmap(resource)
                    showAcy.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            if (!isEmpty(jumpUrl)) {
                                val intent = Intent(this@MainActivity, WebViewActivity::class.java)
                                val sb = StringBuilder()
                                sb.append(jumpUrl)
                                if (jumpUrl.contains("?")) {
                                    sb.append("&token=")
                                } else {
                                    sb.append("?token=")
                                }
                                sb.append(SMApplication.instance.token)
                                intent.putExtra("url", sb.toString())
                                intent.putExtra("from", "首页")
//                                startActivityForResult(intent, 111)
                                startActivityLaunch.launch(intent)
                            } else {
                                ToastUtils.show("链接不存在！")
                            }
                            dialog.doDismiss()
                        }

                        override fun OnMoreErrorClick() {}
                    })
                    val closeAct: ImageView = v.findViewById(R.id.act_close_img)
                    closeAct.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            dialog.doDismiss()
                            if (showActivityIndex < dialogActivityList!!.size - 1) {
                                showActivityIndex++
                                dialogShow(showActivityIndex)
                            }
                        }

                        override fun OnMoreErrorClick() {}
                    })
                }
                    .setCancelable(false)
                    .setFullScreen(false)
                    .setCustomLayoutParams(RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }

        }
    }


    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0
    public override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 0) { //这里是取出我们返回栈存在Fragment的个数
            val secondTime: Long = System.currentTimeMillis()
            if (secondTime - firstTime > 2000) {
                ToastUtils.show("再按一次退出应用")
                firstTime = secondTime
                return
            } else {
//                LocationManagerUtil.self?.stopLocation()
//                LocationManagerUtil.self?.destroyLocation()
                finish()
                exitProcess(0)
            }
        } else { //取出我们返回栈保存的Fragment,这里会从栈顶开始弹栈
            supportFragmentManager.popBackStack()
        }
    }
}

fun BottomNavigationViewEx.init(navigationItemSelectedAction: (Int) -> Unit): BottomNavigationViewEx {
    enableAnimation(true)
    enableShiftingMode(false)
    enableItemShiftingMode(true)
//    itemIconTintList = SettingUtil.getColorStateList(SettingUtil.getColor(appContext))
//    itemTextColor = SettingUtil.getColorStateList(appContext)
    setTextSize(12F)
    setOnNavigationItemSelectedListener {
        navigationItemSelectedAction.invoke(it.itemId)
        true
    }
    return this
}


/**
 * 拦截BottomNavigation长按事件 防止长按时出现Toast ---- 追求完美的大屌群友提的bug
 * @receiver BottomNavigationViewEx
 * @param ids IntArray
 */
fun BottomNavigationViewEx.interceptLongClick(vararg ids:Int) {
    val bottomNavigationMenuView: ViewGroup = (this.getChildAt(0) as ViewGroup)
    for (index in ids.indices){
        bottomNavigationMenuView.getChildAt(index).findViewById<View>(ids[index]).setOnLongClickListener {
            true
        }
    }
}