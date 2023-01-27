package com.jqwong.music.api.service

import com.jqwong.music.api.MusicApi
import com.jqwong.music.api.service.interceptor.MusicApiServiceInterceptor
import com.jqwong.music.model.MusicBaseData
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MusicApiService {

    companion object{
        val h = Retrofit.Builder().baseUrl("https://www.kuwo.cn/api/www/")
            .client(OkHttpClient.Builder().addInterceptor(MusicApiServiceInterceptor()).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(MusicApi::class.java)


        val c = OkHttpClient.Builder()
            .connectTimeout(6,TimeUnit.SECONDS)
            .build()
    }



    interface MusicObserver<T> : Observer<MusicBaseData<T>>

    interface BaseObserver<T>:Observer<T>
}