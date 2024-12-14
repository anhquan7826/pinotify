package com.anhquan.pinotify.view.status

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anhquan.pinotify.model.PlayIntegrityStatus
import com.anhquan.pinotify.util.AppUtil
import com.anhquan.pinotify.util.generateRandomString
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenResponse
import com.google.android.play.core.integrity.StandardIntegrityManager.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class PlayIntegrityStatusViewModel : ViewModel() {
    private val _isReady = MutableLiveData(false)
    val isReady: LiveData<Boolean> get() = _isReady

    private val _status = MutableLiveData(PlayIntegrityStatus())
    val status: LiveData<PlayIntegrityStatus> get() = _status

    private val _requestHash: String = AppUtil.generateRandomString()
    private lateinit var _tokenProvider: StandardIntegrityTokenProvider

    suspend fun initiate(context: Context) {
        _isReady.value = false
        while (!_isReady.value!!) {
            try {
                initTokenProvider(context)
                val integrityToken = getIntegrityToken()
                _isReady.value = true
            } catch (_: Exception) {
                continue
            }
        }
    }

    private suspend fun initTokenProvider(context: Context) {
        if (!this::_tokenProvider.isInitialized) {
            _tokenProvider =
                IntegrityManagerFactory.createStandard(context).prepareIntegrityToken(
                    PrepareIntegrityTokenRequest
                        .builder()
                        .setCloudProjectNumber(1231) // TODO: Set cloud project number.
                        .build()
                ).await()
        }
    }

    private suspend fun getIntegrityToken(): StandardIntegrityToken {
        return _tokenProvider.request(
            StandardIntegrityTokenRequest
                .builder()
                .setRequestHash(_requestHash)
                .build()
        ).await()
    }
}