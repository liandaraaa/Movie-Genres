package com.lianda.movies.utils.di

import android.app.Application
import com.lianda.movies.BuildConfig
import com.lianda.movies.data.api.interceptor.HeaderInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache, loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            ).build()

        return OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(spec))
            .cache(cache)
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        return logging
    }

}