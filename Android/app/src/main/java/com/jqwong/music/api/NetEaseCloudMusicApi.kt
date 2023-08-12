package com.jqwong.music.api

import com.jqwong.music.api.entity.netEase.*
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface NetEaseCloudMusicApi {
    @POST
    fun search(@Url url:String, @Query("params") params:String):Call<BaseResponse<SongList>>
    // https://interface.music.163.com/eapi/cloudsearch/pc

    @POST("api/toplist")
    fun getLeaderboard():Call<BaseResponseX<List<Leaderboard>>>

    @POST("api/v6/playlist/detail?s=8&n=10000")
    fun getPlayListDetail(@Query("id") id:Long):Call<BaseResponseZ<PlayList>>

    @POST
    fun getPlayUrl(@Url url: String, @Query("params") params:String):Call<BaseResponseM<List<PlayUrl>>>

    @POST("weapi/v1/artist/songs")
    fun getArtistSongList(@Query("params") params:String,@Query("encSecKey")encSecKey:String):Call<BaseResponseO<List<Song>>>

    @POST("api/song/lyric?_nmclfl=1&tv=-1&lv=-1&rv=-1&kv=-1")
    fun getLyrics(@Query("id") id:String):Call<LyricResponse>

    @POST("/weapi/login/qrcode/unikey")
    fun getLoginUniKey(@Body content:RequestBody):Call<UniKey>

    @POST("/weapi/login/qrcode/client/login")
    fun loginCheck(@Body content:RequestBody):Call<CheckLoginResponse>

    @POST("/weapi/v1/discovery/recommend/resource")
    fun GetRecommendSongSheet(@Body content: RequestBody):Call<RecommendSheetResult>
}