package com.iq_academy.malika_ai.module.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.iq_academy.malika_ai.database.AppDatabase
import com.iq_academy.malika_ai.database.ChatDao
import com.iq_academy.malika_ai.module.service.ApiService1
import com.iq_academy.malika_ai.module.service.ApiService2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    val IS_TESTER1 = true
    val SERVER_DEVELOPMENT1 = "https://api.openai.com/"
    val SERVER_PRODUCTION1 = "https://api.openai.com/"

    val IS_TESTER2 = true
    val SERVER_DEVELOPMENT2 = "http://3.91.203.222/"
    val SERVER_PRODUCTION2 = "http://3.91.203.222/"

    /**
     * Retrofit Related
     */
    @Provides
    fun server1(): String = if (IS_TESTER1) SERVER_DEVELOPMENT1 else SERVER_PRODUCTION1

    @Provides
    fun server2(): String = if (IS_TESTER2) SERVER_DEVELOPMENT2 else SERVER_PRODUCTION2

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    @Named("Retrofit1")
    fun getRetrofit1(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(server1())
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    @Named("Retrofit2")
    fun getRetrofit2(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(server2())
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()


    @Provides
    @Singleton
    fun getApiService1(@Named("Retrofit1") retrofit: Retrofit): ApiService1 =
        retrofit.create(ApiService1::class.java)

    @Provides
    @Singleton
    fun getApiService2(@Named("Retrofit2") retrofit: Retrofit): ApiService2 =
        retrofit.create(ApiService2::class.java)

    @Provides
    @Singleton
    fun getClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(Interceptor { chain ->
            val builder = chain.request().newBuilder()
            chain.proceed(builder.build())
        })
        .build()


    /**
     * Retrofit Room
     */

    @Provides
    @Singleton
    fun appDatabase(contect: Application): AppDatabase {
        return AppDatabase.getAppDBInstance(contect)
    }

    @Provides
    @Singleton
    fun callDao(appDtabase: AppDatabase): ChatDao {
        return appDtabase.chatDao()
    }
}

