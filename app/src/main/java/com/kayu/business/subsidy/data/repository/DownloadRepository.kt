package com.kayu.business.subsidy.data.repository

import com.kayu.business.subsidy.api.UnionAPIService
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class DownloadRepository @Inject constructor(
    private val mService: UnionAPIService
) {

    suspend fun checkUpgrade(fileUrl: String): ResponseBody{
        return mService.imgDownload(fileUrl)
    }
}