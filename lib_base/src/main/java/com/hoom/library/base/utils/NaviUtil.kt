package com.hoom.library.base.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.ToastUtils
import com.hoom.library.base.model.MapInfoModel
import com.kayu.utils.LogUtil
import com.kayu.utils.location.CoordinateTransformUtil
import com.kongzue.dialog.interfaces.OnMenuItemClickListener
import com.kongzue.dialog.v3.BottomMenu
import java.lang.Exception
import java.net.URISyntaxException

/**
 *
 */
object NaviUtil {
    fun toNavi(
        context: Context,
        latitude: String,
        longtitude: String,
        address: String,
        flag: String?
    ) {
        val mapList = getMapInfoModels(context) ?: return
        val menuArr = ArrayList<CharSequence?>()
        for (model: MapInfoModel in mapList) {
            menuArr.add(model.mapName)
        }
        val bdCoordinate: DoubleArray
        val gcj02Coordinate: DoubleArray
        when (flag) {
            "WGS84" -> {
                bdCoordinate =
                    CoordinateTransformUtil.wgs84tobd09(longtitude.toDouble(), latitude.toDouble())
                gcj02Coordinate =
                    CoordinateTransformUtil.wgs84togcj02(longtitude.toDouble(), latitude.toDouble())
            }
            "GCJ02" -> {
                bdCoordinate =
                    CoordinateTransformUtil.gcj02tobd09(longtitude.toDouble(), latitude.toDouble())
                gcj02Coordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
            }
            "BD09" -> {
                gcj02Coordinate =
                    CoordinateTransformUtil.bd09togcj02(longtitude.toDouble(), latitude.toDouble())
                bdCoordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
            }
            else -> {
                gcj02Coordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
                bdCoordinate = doubleArrayOf(longtitude.toDouble(), latitude.toDouble())
            }
        }
        BottomMenu.show((context as AppCompatActivity), menuArr, object : OnMenuItemClickListener {
            override fun onClick(text: String, index: Int) {
                when (text) {
                    "高德地图" -> goGaodeMap(
                        context,
                        gcj02Coordinate[1].toString(),
                        gcj02Coordinate[0].toString(),
                        address
                    )
                    "谷歌地图" -> goGoogleMap(
                        context,
                        gcj02Coordinate[1].toString(),
                        gcj02Coordinate[0].toString(),
                        address
                    )
                    "百度地图" -> goBaiduMap(
                        context,
                        bdCoordinate[1].toString(),
                        bdCoordinate[0].toString(),
                        address
                    )
                    "腾讯地图" -> goTencentMap(
                        context,
                        gcj02Coordinate[1].toString(),
                        gcj02Coordinate[0].toString(),
                        address
                    )
                }
            }
        })
    }

    private fun getMapInfoModels(context: Context): List<MapInfoModel>? {
        val mapList: MutableList<MapInfoModel> = ArrayList()
        if (isNavigationApk(context, "com.autonavi.minimap")) {
            val model = MapInfoModel()
            model.mapId = "0"
            model.mapName = "高德地图"
            mapList.add(model)
        }
        if (isNavigationApk(context, "com.google.android.apps.maps")) {
            val model = MapInfoModel()
            model.mapId = "1"
            model.mapName = "谷歌地图"
            mapList.add(model)
        }
        if (isNavigationApk(context, "com.baidu.BaiduMap")) {
            val model = MapInfoModel()
            model.mapId = "2"
            model.mapName = "百度地图"
            mapList.add(model)
        }
        if (isNavigationApk(context, "com.tencent.map")) {
            val model = MapInfoModel()
            model.mapId = "3"
            model.mapName = "腾讯地图"
            mapList.add(model)
        }
        if (mapList.size == 0) {
            ToastUtils.show("您尚未安装导航APP")
            return null
        }
        return mapList
    }

    /**
     * 判断手机中是否有导航app
     *
     * @param context
     * @param packagename 包名
     */
    fun isNavigationApk(context: Context, packagename: String): Boolean {
        val packages = context.packageManager.getInstalledPackages(0)
        for (i in packages.indices) {
            val packageInfo = packages[i]
            return if ((packageInfo.packageName == packagename)) {
                true
            } else {
                continue
            }
        }
        return false
    }

    /**
     * 跳转到百度地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goBaiduMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.baidu.BaiduMap")) {
            try {
                val intent = Intent.getIntent(
                    ("intent://map/direction?destination=latlng:"
                            + latitude + ","
                            + longtitude + "|name:" + address +  //终点：该地址会在导航页面的终点输入框显示
                            "&mode=driving&" +  //选择导航方式 此处为驾驶
                            "region=" +  //
                            "&src=#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end")
                )
                activity.startActivity(intent)
            } catch (e: URISyntaxException) {
                LogUtil.e("goError", e.message)
            }
        } else {
            ToastUtils.show("您尚未安装百度地图")
        }
    }

    /**
     * 跳转到高德地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goGaodeMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            try {
                val intent = Intent.getIntent(
                    ("androidamap://navi?sourceApplication=&poiname=" + address + "&lat=" + latitude
                            + "&lon=" + longtitude + "&dev=0")
                )
                activity.startActivity(intent)
            } catch (e: URISyntaxException) {
                LogUtil.e("goError", e.message)
            }
        } else {
            ToastUtils.show("您尚未安装高德地图")
        }
    }

    /**
     * 跳转到谷歌地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goGoogleMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        ("http://ditu" +
                                ".google" + ".cn/maps?hl=zh&mrt=loc&q=" + latitude + "," + longtitude + "(" + address + ")")
                    )
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                activity.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            ToastUtils.show("您尚未安装谷歌地图")
        }
    }

    /**
     * 跳转到腾讯地图
     *
     * @param activity
     * @param latitude   纬度
     * @param longtitude 经度
     * @param address    终点
     */
    private fun goTencentMap(
        activity: Context,
        latitude: String,
        longtitude: String,
        address: String
    ) {
        if (isNavigationApk(activity, "com.autonavi.minimap")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(
                ("qqmap://map/routeplan?type=bus&from=我的位置&fromcoord=0,0"
                        + "&to=" + address
                        + "&tocoord=" + latitude + "," + longtitude
                        + "&policy=1&referer=myapp")
            )
            activity.startActivity(intent)
        } else {
            ToastUtils.show("您尚未安装腾讯地图")
        }
    }
}