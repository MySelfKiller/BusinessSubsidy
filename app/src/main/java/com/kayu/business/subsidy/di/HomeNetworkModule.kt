package com.kayu.business.subsidy.di

import com.kayu.business.subsidy.api.UnionAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class HomeNetworkModule {

    /**
     * Home模块的[UnionAPIService]依赖提供方法
     *
     * @param retrofit Retrofit
     * @return TeGoService
     */
    @Singleton
    @Provides
    fun provideHomeApiService(retrofit: Retrofit): UnionAPIService {
        return retrofit.create(UnionAPIService::class.java)
    }

}
