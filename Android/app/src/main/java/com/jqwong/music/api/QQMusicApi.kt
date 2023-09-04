package com.jqwong.music.api

import com.jqwong.music.api.entity.qq.PlayUrl
import com.jqwong.music.api.entity.qq.SearchResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface QQMusicApi {

    @POST("/cgi-bin/musicu.fcg")
    fun search(@Body body: RequestBody): Call<SearchResponse>

    @POST
    fun getPlayUrl(@Url url:String,@Body body: RequestBody):Call<PlayUrl>
}