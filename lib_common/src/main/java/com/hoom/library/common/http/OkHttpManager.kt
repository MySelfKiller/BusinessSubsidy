package com.hoom.library.common.http

import android.webkit.WebSettings
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.hoom.library.base.BaseApplication
import com.hoom.library.common.http.okHttpLog.HttpLoggingInterceptorM
import com.hoom.library.common.http.okHttpLog.LogInterceptor
import com.hoom.library.common.network.interceptor.CacheInterceptor
import com.kayu.utils.LogUtil
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.io.Serializable
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * modify by Hom（Chinese name's HuangMin） on 2022/3/11.
 */
object OkHttpManager : Serializable{
    private val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(BaseApplication.context))
    }
    private lateinit var mClient: OkHttpClient
    val httpClient: OkHttpClient
        get() {
            return mClient
        }
    private fun readResolve(): Any {//防止单例对象在反序列化时重新生成对象
        return OkHttpManager//由于反序列化时会调用readResolve这个钩子方法，只需要把当前的KSingleton对象返回而不是去创建一个新的对象
    }

    init {
        initOkhttpClient()
    }

    /**
     * 初始化okhttp
     */
    private fun initOkhttpClient() {
        LogUtil.e("hm", "执行OkHttpClient初始化")
//        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val loggerInterceptor = HttpLoggingInterceptorM(LogInterceptor())
        loggerInterceptor.level = HttpLoggingInterceptorM.Level.BODY

        val okBuilder = OkHttpClient.Builder()
        //设置缓存配置 缓存最大10M
        okBuilder.cache(Cache(File(BaseApplication.context.cacheDir, "cxk_cache"), 10 * 1024 * 1024))
        okBuilder.connectTimeout(10, TimeUnit.SECONDS)
        okBuilder.readTimeout(10, TimeUnit.SECONDS)
        okBuilder.writeTimeout(10, TimeUnit.SECONDS)
        okBuilder.cookieJar(cookieJar)
//        okBuilder.addInterceptor(CacheInterceptor())
        //示例：添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息
        //okBuilder.addInterceptor(MyHeadInterceptor())
        //添加缓存拦截器 可传入缓存天数，不传默认7天
        okBuilder.addInterceptor(CacheInterceptor())
        //token过期拦截
        //okBuilder.addInterceptor(TokenOutInterceptor())
        // 日志拦截器
        okBuilder.addInterceptor(loggerInterceptor)
        okBuilder.addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .removeHeader("User-Agent") //移除旧的
                //                                                 WebSettings.getDefaultUserAgent(mContext) 是获取原来的User-Agent
                .addHeader(
                    "User-Agent",
                    WebSettings.getDefaultUserAgent(BaseApplication.context)
                )
                .addHeader("terminal","android")
                .addHeader("Referer",HttpConfig.HOST)
                .build()
            chain.proceed(request)
        }
        okBuilder.hostnameVerifier { hostname, session -> true }
        if (HttpConfig.HOST.startsWith("https")) {
            var contextSSL: SSLContext? = null
            try {
                contextSSL = SSLContext.getInstance("TLS")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            try {
                contextSSL!!.init(null, arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }), SecureRandom())
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            okBuilder.sslSocketFactory(
                TrustAllSSLSocketFactory.defaultfactory,
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                })
        }
        mClient = okBuilder.build()
    }
}