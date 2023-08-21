package com.jqwong.music.api

import com.jqwong.music.api.entity.netEase.*
import okhttp3.RequestBody
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

    @POST("api/toplist")
    fun getLeaderboard():Call<BaseResponseX<List<Leaderboard>>>

    @POST("api/v6/playlist/detail?s=8&n=10000")
    fun getPlayListDetail(@Query("id") id:Long):Call<BaseResponseZ<PlayListDetail>>

    @POST("/weapi/v3/song/detail")
    fun getPlayList(@Body content:RequestBody):Call<BaseResponseO<List<Song>>>

    @POST
    fun getPlayUrl(@Url url: String, @Query("params") params:String):Call<BaseResponseM<List<PlayUrl>>>

    @POST("weapi/v1/artist/songs")
    fun getArtistSongList(@Body content:RequestBody):Call<BaseResponseO<List<Song>>>

    @POST("api/song/lyric?_nmclfl=1&tv=-1&lv=-1&rv=-1&kv=-1")
    fun getLyrics(@Query("id") id:String):Call<LyricResponse>

    @POST("/weapi/login/qrcode/unikey")
    fun getLoginUniKey(@Body content:RequestBody):Call<UniKey>

    @POST("/weapi/login/qrcode/client/login")
    fun loginCheck(@Body content:RequestBody):Call<CheckLoginResponse>

    @POST("/weapi/v1/discovery/recommend/resource")
    fun getRecommendSongSheet(@Body content: RequestBody):Call<RecommendSheetResult>

    @POST("/weapi/v1/radio/get")
    fun personalFM(@Body content: RequestBody):Call<BaseResponseM<List<SongX>>>

    @POST("/weapi/v3/discovery/recommend/songs")
    fun getDailySongs(@Body content: RequestBody):Call<BaseResponseM<DailySongs>>

    @POST("/weapi/song/enhance/play/mv/url")
    fun getMvUrl(@Body content: RequestBody):Call<BaseResponseM<MvUrl>>

    @POST("/weapi/user/playlist")
    fun getUserSheet(@Body content: RequestBody):Call<BaseResponseZ<List<UserSheet>>>

    @POST("/weapi/nuser/account/get")
    fun getUserInfo(@Body content: RequestBody):Call<UserResponse>
}