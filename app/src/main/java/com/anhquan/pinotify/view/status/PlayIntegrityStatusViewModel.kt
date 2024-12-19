package com.anhquan.pinotify.view.status

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.core.os.BuildCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anhquan.pinotify.BuildConfig
import com.anhquan.pinotify.R
import com.anhquan.pinotify.model.PlayIntegrityStatus
import com.anhquan.pinotify.util.AppUtil
import com.anhquan.pinotify.util.generateRandomString
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenResponse
import com.google.android.play.core.integrity.StandardIntegrityManager.*
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.playintegrity.v1.PlayIntegrity
import com.google.api.services.playintegrity.v1.model.DecodeIntegrityTokenRequest
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.FileInputStream

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
                initPlayIntegrity(context)
                val integrityToken = getIntegrityToken()
                Log.d("PlayIntegrityStatusViewModel", integrityToken.token())
                attestIntegrity(context, integrityToken)
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
                        .setCloudProjectNumber(BuildConfig.CLOUD_PROJECT_NUMBER)
                        .build()
                ).await()
        }
    }

    private fun initPlayIntegrity(context: Context) {
        val creds = GoogleCredentials.fromStream(
            context.resources.openRawResource(R.raw.credentials) //TODO: read from somewhere else
        ) // TODO: add service account credential.
        creds.refreshIfExpired()

        if (!this::_playIntegrity.isInitialized) {
            _playIntegrity = PlayIntegrity.Builder(
                NetHttpTransport.Builder().build(),
                GsonFactory(),
                HttpCredentialsAdapter(creds)
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

    private fun attestIntegrity(context: Context, token: StandardIntegrityToken) {
        val decodedPlayIntegrityToken = _playIntegrity.v1().decodeIntegrityToken(
            context.packageName,
            DecodeIntegrityTokenRequest().setIntegrityToken(token.token())
        )
        Log.d("DecodePlayIntegrityToken", decodedPlayIntegrityToken.toString())
    }
}