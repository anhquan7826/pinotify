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
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.playintegrity.v1.PlayIntegrity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class PlayIntegrityStatusViewModel : ViewModel() {
    private val _isReady = MutableLiveData(false)
    val isReady: LiveData<Boolean> get() = _isReady

    private val _status = MutableLiveData(PlayIntegrityStatus())
    val status: LiveData<PlayIntegrityStatus> get() = _status

    private val _requestHash: String = AppUtil.generateRandomString()
    private lateinit var _tokenProvider: StandardIntegrityTokenProvider
    private lateinit var _playIntegrity: PlayIntegrity

    suspend fun initiate(context: Context) {
        _isReady.value = false
        while (!_isReady.value!!) {
            try {
                initTokenProvider(context)
                val integrityToken = getIntegrityToken()
                Log.d("PlayIntegrityStatusViewModel", integrityToken.token())
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
                        .setCloudProjectNumber(462837158343) // TODO: Set cloud project number.
                        .build()
                ).await()
        }
    }

    private suspend fun initPlayIntegrity() {
        if (!this::_playIntegrity.isInitialized) {
            _playIntegrity = PlayIntegrity.Builder(
                NetHttpTransport.Builder().build(),
                GsonFactory(),
                null
            ).build()
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

    private suspend fun attestIntegrity(token: StandardIntegrityToken) {

    }
}