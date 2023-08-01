package com.jqwong.music.api

import com.jqwong.music.api.entity.netEase.BaseResponse
import com.jqwong.music.api.entity.netEase.BaseResponseM
import com.jqwong.music.api.entity.netEase.BaseResponseO
import com.jqwong.music.api.entity.netEase.BaseResponseX
import com.jqwong.music.api.entity.netEase.BaseResponseZ
import com.jqwong.music.api.entity.netEase.Leaderboard
import com.jqwong.music.api.entity.netEase.LyricResponse
import com.jqwong.music.api.entity.netEase.PlayList
import com.jqwong.music.api.entity.netEase.PlayUrl
import com.jqwong.music.api.entity.netEase.Song
import com.jqwong.music.api.entity.netEase.SongList
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface NetEaseCloudMusicApi {
    @POST
    fun search(@Query("params") params:String):Call<BaseResponse<SongList>>
    // https://interface.music.163.com/eapi/cloudsearch/pc

    @POST("api/toplist")
    fun getLeaderboard():Call<BaseResponseX<List<Leaderboard>>>

    @POST("api/v6/playlist/detail")
    fun getPlayListDetail(@Query("id") id:Long):Call<BaseResponseZ<PlayList>>

    @POST("weapi/song/enhance/play/mv/url")
    fun getPlayUrl(@Query("params") params:String,@Query("encSecKey")encSecKey:String):Call<BaseResponseM<PlayUrl>>

    @POST("weapi/v1/artist/songs")
    fun getArtistSongList(@Query("params") params:String,@Query("encSecKey")encSecKey:String):Call<BaseResponseO<List<Song>>>

    @POST("api/song/lyric?_nmclfl=1&tv=-1&lv=-1&rv=-1&kv=-1")
    fun getLyrics(@Query("id") id:String):Call<LyricResponse>
}