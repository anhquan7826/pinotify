package com.anhquan.pinotify.data

import android.content.Context
import com.anhquan.pinotify.R
import com.google.android.play.core.integrity.IntegrityManager
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.playintegrity.v1.PlayIntegrity
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGoogleCredentials(@ApplicationContext context: Context): GoogleCredentials {
        return GoogleCredentials.fromStream(
            context.resources.openRawResource(R.raw.credentials)
        ).createScoped("https://www.googleapis.com/auth/playintegrity")
    }

    @Provides
    @Singleton
    fun provideIntegrityManager(@ApplicationContext context: Context): IntegrityManager {
        return IntegrityManagerFactory.create(context)
    }

    @Provides
    @Singleton
    fun providePlayIntegrity(
        @ApplicationContext context: Context,
        googleCredentials: GoogleCredentials
    ): PlayIntegrity {
        return PlayIntegrity.Builder(
            NetHttpTransport.Builder().build(),
            GsonFactory(),
            HttpCredentialsAdapter(googleCredentials)
        ).build()
    }
}