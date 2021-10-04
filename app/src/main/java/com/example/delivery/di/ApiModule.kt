package com.example.delivery.di

import com.example.delivery.data.api.FoodApiService
import com.example.delivery.data.api.TmapApiService
import com.example.delivery.data.url.Url
import com.example.delivery.util.FOOD_RETROFIT
import com.example.delivery.util.TMAP_RETROFIT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val apiModule : Module = module {

    single<TmapApiService> { get<Retrofit>(named(TMAP_RETROFIT)).create(TmapApiService::class.java) }

    single<FoodApiService> { get<Retrofit>(named(FOOD_RETROFIT)).create(FoodApiService::class.java) }

    single<Retrofit>(named(TMAP_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl(Url.TMAP_URL)
            .addConverterFactory(get<GsonConverterFactory>())
            .client(get<OkHttpClient>())
            .build()
    }

    single<Retrofit>(named(FOOD_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl(Url.FOOD_URL)
            .addConverterFactory(get<GsonConverterFactory>())
            .client(get<OkHttpClient>())
            .build()
    }

    single<GsonConverterFactory> { GsonConverterFactory.create() }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .apply {
                connectTimeout(5, TimeUnit.SECONDS)
                addInterceptor(get<HttpLoggingInterceptor>())
            }
            .build()
    }

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
}