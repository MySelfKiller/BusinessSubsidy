package com.hoom.library.common.di

import com.hoom.library.common.http.HttpConfig
import com.hoom.library.common.http.OkHttpManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * 全局作用域的网络层的依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
class DINetworkModule {

//    /**
//     * [OkHttpClient]依赖提供方法
//     *
//     * @return OkHttpClient
//     */
//    @Singleton
//    @Provides
//    fun provideOkHttpClient(): OkHttpClient {
//        // 日志拦截器部分
//        val level = if (BuildConfig.VERSION_TYPE != VersionStatus.RELEASE) BODY else NONE
//        val logInterceptor = HttpLoggingInterceptor().setLevel(level)
//
//        return OkHttpClient.Builder()
//            .connectTimeout(15L * 1000L, TimeUnit.MILLISECONDS)
//            .readTimeout(20L * 1000L, TimeUnit.MILLISECONDS)
//            .addInterceptor(logInterceptor)
//            .retryOnConnectionFailure(true)
//            .build()
//    }

    /**
     * 项目主要服务器地址的[Retrofit]依赖提供方法
     *
     * @param okHttpClient OkHttpClient OkHttp客户端
     * @return Retrofit
     */
    @Singleton
    @Provides
    fun provideMainRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(HttpConfig.HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpManager.httpClient)
            .build()
    }
}