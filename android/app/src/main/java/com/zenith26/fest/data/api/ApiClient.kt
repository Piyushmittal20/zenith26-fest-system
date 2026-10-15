package com.zenith26.fest.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Manages HTTP networking, Retrofit initialization, and base URL configuration.
 */
object ApiClient {

    /**
     * Default development base URL.
     * http://10.0.2.2:8000/ routes to the host computer's localhost from an Android emulator.
     * When running on a physical phone, set this to your computer's LAN IP (e.g. http://192.168.1.15:8000/).
     */
    const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/"

    private var currentBaseUrl: String = DEFAULT_BASE_URL
    private var retrofitInstance: Retrofit? = null
    private var serviceInstance: ZenithApiService? = null

    fun getBaseUrl(): String = currentBaseUrl

    /**
     * Update the API base URL at runtime without rebuilding.
     */
    fun setBaseUrl(newUrl: String) {
        val sanitized = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (sanitized != currentBaseUrl) {
            currentBaseUrl = sanitized
            retrofitInstance = null
            serviceInstance = null
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    private fun getRetrofit(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            val instance = Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitInstance = instance
            instance
        }
    }

    val service: ZenithApiService
        get() = serviceInstance ?: synchronized(this) {
            val instance = getRetrofit().create(ZenithApiService::class.java)
            serviceInstance = instance
            instance
        }
}
