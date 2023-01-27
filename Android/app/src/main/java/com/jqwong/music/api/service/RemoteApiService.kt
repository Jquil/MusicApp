package com.jqwong.music.api.service

import com.jqwong.music.api.MusicApi
import com.jqwong.music.api.RemoteApi
import com.jqwong.music.api.factory.NullOnEmptyConverterFactory
import com.jqwong.music.api.service.interceptor.MusicApiServiceInterceptor
import com.jqwong.music.api.service.interceptor.RemoteApiServiceInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RemoteApiService {


    companion object{
        val h = Retrofit.Builder().baseUrl("https://cf937oi5g6h66drd9h1g.baseapi.memfiredb.com/rest/v1/")
            .client(OkHttpClient.Builder().addInterceptor(RemoteApiServiceInterceptor()).build())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(RemoteApi::class.java)
    }
}