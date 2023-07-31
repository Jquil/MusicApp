package com.jqwong.music.service

import com.jqwong.music.api.KuWoMusicApi
import com.jqwong.music.api.entity.kuwo.BaseResponse
import com.jqwong.music.api.entity.kuwo.SongListX
import com.jqwong.music.app.App
import com.jqwong.music.helper.NetHelper
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.helper.awaitResult
import com.jqwong.music.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class KuWOService:IService {

    companion object{
        private val service = Retrofit.Builder()
            .baseUrl("https://www.kuwo.cn/api/")
            .client(OkHttpClient.Builder()
                .connectTimeout(App.config.okhttp_request_timeout,TimeUnit.MILLISECONDS)
                .addInterceptor {
                    val req = it.request()
                    val builder = req.newBuilder()
                    App.config.kuWoMusicConfig.cookies.forEach { s, s2 ->
                        builder.header(s,s2)
                    }
                    it.proceed(builder.build())
                }
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build())
            .addConverterFactory(MoshiConverterFactory
                .create(Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()))
            .build()
            .create(KuWoMusicApi::class.java)
    }

    override fun GetLeaderboard(): Response<Leaderboard> {
        TODO("Not yet implemented")
    }

    override fun GetLeaderboardSongList(id: Long, page: Int, limit: Int): Response<List<Song>> {
        TODO("Not yet implemented")
    }

    override fun GetArtistSongList(id: Long, page: Int, limit: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun Search(key: String, page: Int, limit: Int): Response<List<Song>> {
        val flag = "Search"
        val data = service.Search(key,page,limit).awaitResult()
        if(data.e != null){
            return Response<List<Song>>(
                title = flag,
                success = false,
                message = "",
                data = null,
                exception = ExceptionLog(
                    title = flag,
                    message = data.e.message.toString(),
                    time = TimeHelper.getTime()
                )
            )
        }
        else{
            val list = mutableListOf<Song>()
            data.data!!.data.list.forEach {
                list.add(it.convert())
            }
            return Response<List<Song>>(
                title = flag,
                success = true,
                message = "ok",
                data = list,
                exception = null
            )
        }
    }

    override fun GetRecommendSongSheetList(data: Any): Response<RecommendSongSheet> {
        TODO("Not yet implemented")
    }

    override fun GetRecommendSongSheetData(data: Any, page: Int, limit: Int): Response<List<Song>> {
        TODO("Not yet implemented")
    }

    override fun GetRecommendDaily(data: Any): Response<List<Song>> {
        TODO("Not yet implemented")
    }

    override fun GetPlayUrl(id: Long, quality: Any) {
        TODO("Not yet implemented")
    }

    override fun GetMvUrl(id: Long) {
        TODO("Not yet implemented")
    }

    override fun GetLyrics(id: Long) {
        TODO("Not yet implemented")
    }
}