package com.jqwong.music.api

import com.jqwong.music.api.entity.qq.ArtistSongListData
import com.jqwong.music.api.entity.qq.ArtistSongListResponse
import com.jqwong.music.api.entity.qq.LyricsResponse
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

    @POST("/cgi-bin/musicu.fcg")
    fun getLyrics(@Body body: RequestBody):Call<LyricsResponse>

    @POST("/cgi-bin/musicu.fcg")
    fun getArtistSongList(@Body body: RequestBody):Call<ArtistSongListResponse>
}