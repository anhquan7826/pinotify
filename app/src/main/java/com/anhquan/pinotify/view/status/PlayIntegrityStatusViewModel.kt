package com.anhquan.pinotify.view.status

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anhquan.pinotify.R
import com.anhquan.pinotify.model.PlayIntegrityStatus
import com.anhquan.pinotify.model.PlayIntegrityVerdict
import com.anhquan.pinotify.util.AppUtil
import com.anhquan.pinotify.util.generateRandomString
import com.google.android.play.core.integrity.IntegrityManager
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import com.google.android.play.core.integrity.StandardIntegrityManager.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.playintegrity.v1.PlayIntegrity
import com.google.api.services.playintegrity.v1.model.DecodeIntegrityTokenRequest
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PlayIntegrityStatusViewModel @Inject constructor(
    private val playIntegrity: PlayIntegrity,
    private val integrityManager: IntegrityManager
) : ViewModel() {
    private val _isReady = MutableLiveData(false)
    val isReady: LiveData<Boolean> get() = _isReady

    private val _status = MutableLiveData(PlayIntegrityStatus())
    val status: LiveData<PlayIntegrityStatus> get() = _status

    private val _requestHash: String = AppUtil.generateRandomString(64)

    fun initiate(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("PlayIntegrityStatusViewModel", "getting integrity token")
                val integrityToken = getIntegrityToken()
                Log.d(
                    "PlayIntegrityStatusViewModel",
                    "Integrity token got: " + integrityToken.token()
                )
                attestIntegrity(context, integrityToken)
            } catch (e: Exception) {
                Log.e("PlayIntegrityStatusViewModel", "Error: $e")
            }
        }
    }

    private suspend fun getIntegrityToken(): IntegrityTokenResponse {
        return integrityManager.requestIntegrityToken(
            IntegrityTokenRequest
                .builder()
                .setNonce(_requestHash)
                .setCloudProjectNumber(462837158343)
                .build()
        ).await()
    }

    private fun attestIntegrity(context: Context, token: IntegrityTokenResponse) {
        Log.d("PlayIntegrityStatusViewModel", "attestIntegrity")
        val decodedPlayIntegrityToken = playIntegrity.v1().decodeIntegrityToken(
            context.packageName,
            DecodeIntegrityTokenRequest()
                .setIntegrityToken(token.token())
        )
        val response = decodedPlayIntegrityToken.execute().toString()
        Gson().fromJson(response, PlayIntegrityVerdict::class.java)
    }
}