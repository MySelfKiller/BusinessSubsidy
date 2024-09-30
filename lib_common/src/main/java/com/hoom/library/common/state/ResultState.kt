package com.hoom.library.common.state
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.hoom.library.base.BaseApplication
import com.hoom.library.common.network.*
import com.kayu.utils.Constants
import com.kayu.utils.LogUtil

/**
 * 自定义结果集封装类
 */
sealed class ResultState<out T> {
    companion object {
        fun <T> onAppSuccess(data: T): ResultState<T> = Success(data)
        fun <T> onAppLoading(loadingMessage: String): ResultState<T> = Loading(loadingMessage)
        fun <T> onAppError(error: AppException): ResultState<T> = Error(error)
    }

    data class Loading(val loadingMessage: String) : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val error: AppException) : ResultState<Nothing>()
}

/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T> MutableLiveData<ResultState<T>>.paresResult(result: BaseResponse<T>) {
    value = when {
        result.isSucces() -> {
            ResultState.onAppSuccess(result.getResponseData())
        }
        else -> {
            if (result.getResponseCode() == Constants.response_code_10101 || result.getResponseCode() == Constants.response_code_10102) {
                val intent = Intent("com.kayu.business.subsidy.JUMP")
                BaseApplication.context.sendBroadcast(intent)
                LogUtil.e("paresResult","发送需要登录广播${intent.action}")
                ResultState.onAppError(AppException(result.getResponseCode(), result.getResponseMsg()))
            } else {
                ResultState.onAppError(AppException(result.getResponseCode(), result.getResponseMsg()))
            }
        }
    }
}
/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T> MutableLiveData<ResultState<T>>.paresWeatherResult(result: ApiWeatherResponse<T>) {
    value = when {
        result.status == "1" &&result.infocode=="10000"-> {
            ResultState.onAppSuccess(result.lives)
        }
        else -> {
            ResultState.onAppError(AppException(result.status.toInt(), result.msg))
        }
    }
}
/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T,U> MutableLiveData<ResultState<T>>.paresResultT(result: Base2Response<T,U>) {
    value = when {
        result.isSucces() -> {
            ResultState.onAppSuccess(result.getResponseData())
        }
        else -> {
            ResultState.onAppError(AppException(result.getResponseCode(), result.getResponseMsg()))
        }
    }
}
/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T,U> MutableLiveData<ResultState<U>>.paresResultU(result: Base2Response<T,U>) {
    value = when {
        result.isSucces() -> {
            ResultState.onAppSuccess(result.getResponse2Data())
        }
        else -> {
            ResultState.onAppError(AppException(result.getResponseCode(), result.getResponseMsg()))
        }
    }
}

/**
 * 不处理返回值 直接返回请求结果
 * @param result 请求结果
 */
fun <T> MutableLiveData<ResultState<T>>.paresResult(result: T) {
    value = ResultState.onAppSuccess(result)
}

/**
 * 异常转换异常处理
 */
fun <T> MutableLiveData<ResultState<T>>.paresException(e: Throwable) {
    this.value = ResultState.onAppError(ExceptionHandle.handleException(e))
}

