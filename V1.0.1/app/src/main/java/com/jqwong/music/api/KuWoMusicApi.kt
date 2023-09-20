package com.jqwong.music.api

import com.jqwong.music.api.entity.kuwo.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface KuWoMusicApi {
    @GET("www/search/searchMusicBykeyWord?httpsStatus=1&reqId=23016430-e1eb-11eb-a2ee-bf024dbfa4c7")
    fun search(@Query("key") key:String, @Query("pn") pn:Int, @Query("rn") rn:Int):Call<BaseResponse<SongListM>>

    @GET
    fun search2(@Url url:String):Call<SearchResult>

    @GET("www/bang/bang/bangMenu?httpsStatus=1&reqId=e617e730-e1f7-11eb-942d-33e288737b1d")
    fun getLeaderboard():Call<BaseResponse<List<Leaderboard>>>

    @GET("www/bang/bang/musicList?httpsStatus=1&reqId=b092ca30-e152-11eb-90cc-79484e9fbe4d")
    fun getLeaderboardSongList(@Query("bangId") bangId:String, @Query("pn") pn:Int, @Query("rn") rn:Int):Call<BaseResponse<SongList>>

    @GET("www/artist/artistMusic?httpsStatus=1&reqId=87263830-f72d-11eb-979c-c11891b4f2ba")
    fun getArtistSongList(@Query("artistid") artistid:String, @Query("pn") pn:Int, @Query("rn") rn:Int):Call<BaseResponse<SongListM>>

    @GET("www/rcm/index/playlist?httpsStatus=1&reqId=87263830-f72d-11eb-979c-c11891b4f2ba")
    fun getRecommendSongSheet():Call<BaseResponse<RecommendSongSheet>>

    @GET("www/playlist/playListInfo?httpsStatus=1&reqId=87263830-f72d-11eb-979c-c11891b4f2ba&plat=web_www&from=")
    fun getRecommendSongSheetData(@Query("pid") pid:String, @Query("pn") pn:Int, @Query("rn") rn:Int):Call<BaseResponse<SongListX>>

    @GET("v1/www/music/playUrl?type=mv&httpsStatus=1&reqId=d56f0250-401f-11ee-a8a2-1946b837dd11&plat=web_www")
    fun getMvUrl(@Query("mid") mid:String):Call<BaseResponse<MvUrl>>

    @GET("artist/artist?httpsStatus=1&reqId=b06e62f0-f582-11eb-bd8d-c19fac490f25")
    fun getArtistInfo(@Query("artistid") artistid:String):Call<BaseResponse<Artist>>

    @GET
    fun getLyrics(@Url url:String): Call<BaseResponseX<Lyrics>>

    @GET
    fun getPlayUrl(@Url url:String):Call<ResponseBody>
}