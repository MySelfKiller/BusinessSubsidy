package com.kayu.business.subsidy.data.repository

import com.hoom.library.common.network.ApiResponse
import com.kayu.business.subsidy.api.UnionAPIService
import com.kayu.business.subsidy.data.bean.ImageCode
import com.kayu.business.subsidy.data.bean.LoginDataBean
import com.kayu.business.subsidy.data.bean.UpgradeInfo
import com.kayu.utils.LogUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository @Inject constructor(
    private val mService: UnionAPIService
) {
//    suspend fun getSysParam(type: String): ApiResponse<SystemParam>{
//        return mService.getSysParam(type)
//    }

    suspend fun sendSmsCode(phone: String): ApiResponse<Any?> {
        return mService.sendSmsCode(phone)
    }
    suspend fun sendLogoffSmsCode(): ApiResponse<Any?> {
        return mService.sendLogoffSmsCode()
    }

    suspend fun getImageCode(): ApiResponse<ImageCode>{
        return  mService.getImageCode()
    }
    suspend fun sendSetPwdSmsCode(phone: String): ApiResponse<Any?> {
        return mService.sendSetPwdSmsCode(phone)
    }

    suspend fun login(reqDateMap: HashMap<String,Any>): ApiResponse<LoginDataBean>{
        return mService.login(toRequestBody(reqDateMap))
    }
    suspend fun logoff(code: String): ApiResponse<Any?>{
        return mService.logoff(code)
    }
    suspend fun setPassword(reqDateMap: HashMap<String,Any>): ApiResponse<Any?>{
        return mService.setPassword(toRequestBody(reqDateMap))
    }
    suspend fun setForgetPassword(reqDateMap: HashMap<String,Any>): ApiResponse<Any?>{
        return mService.setForgetPassword(toRequestBody(reqDateMap))
    }

//    private fun toRequestBody( vararg params: Pair<String, Any?>): RequestBody {
//        return toJSONObject( *params).toString().toRequestBody("application/json".toMediaTypeOrNull())
//    }

    private fun toRequestBody( reqDateMap: HashMap<*,*>): RequestBody {
        return toJSONObject( reqDateMap).toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

//    private fun toJSONObject(vararg params: Pair<String, Any?>): JSONObject {
//        val param = JSONObject()
//        for (i in params) {
//            val value = if (i.second == null) "" else i.second
//            param.put(i.first, value)
//        }
//        LogUtil.e("http request param：", param.toString())
//        return param
//    }

    private fun toJSONObject(reqDateMap: Map<*,*>): JSONObject {
        val param = JSONObject(reqDateMap)
        LogUtil.e("http request param：", param.toString())
        return param
    }

    suspend fun checkUpgrade(version: String): ApiResponse<UpgradeInfo>{
        return mService.checkUpgrade(version)
    }
}