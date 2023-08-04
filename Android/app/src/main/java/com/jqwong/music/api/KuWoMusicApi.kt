package com.jqwong.music.api

import com.jqwong.music.api.entity.kuwo.Artist
import com.jqwong.music.api.entity.kuwo.BaseResponse
import com.jqwong.music.api.entity.kuwo.BaseResponseX
import com.jqwong.music.api.entity.kuwo.Leaderboard
import com.jqwong.music.api.entity.kuwo.Lyrics
import com.jqwong.music.api.entity.kuwo.MvUrl
import com.jqwong.music.api.entity.kuwo.RecommendSongSheet
import com.jqwong.music.api.entity.kuwo.SongList
import com.jqwong.music.api.entity.kuwo.SongListM
import com.jqwong.music.api.entity.kuwo.SongListX
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
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

    @GET("www/bang/bang/bangMenu?httpsStatus=1&reqId=e617e730-e1f7-11eb-942d-33e288737b1d")
    fun getLeaderboard():Call<BaseResponse<List<Leaderboard>>>

    @GET("www/bang/bang/musicList?httpsStatus=1&reqId=b092ca30-e152-11eb-90cc-79484e9fbe4d")
    fun getLeaderboardSongList(@Query("bangId") bangId:String, @Query("pn") pn:Int, @Query("rn") rn:Int):Call<BaseResponse<SongList>>

    @GET("www/artist/artistMusic?httpsStatus=1&reqId=87263830-f72d-11eb-979c-c11891b4f2ba")
    fun getArtistSongList(@Query("artistid") artistid:String, @Query("pn") pn:Int, @Query("rn") rn:Int):Call<BaseResponse<SongListM>>

    @GET("www/rcm/index/playlist?pn=1&rn=20&httpsStatus=1")
    fun getRecommendSongSheet():Call<BaseResponse<RecommendSongSheet>>

    @GET("www/playlist/playListInfo?httpsStatus=1&pid=1082685104&pn=1&rn=20")
    fun getRecommendSongList(@Query("artistid") artistid:String, @Query("pn") pn:Int, @Query("rn") rn:Int):Call<BaseResponse<SongListX>>

    @GET("www/music/playUrl?type=mv&httpsStatus=1&reqId=db3f8670-e1f6-11eb-942d-33e288737b1d&plat=web_www&from=")
    fun getMvUrl(@Query("mid") mid:String):BaseResponse<MvUrl>

    @GET("artist/artist?httpsStatus=1&reqId=b06e62f0-f582-11eb-bd8d-c19fac490f25")
    fun getArtistInfo(@Query("artistid") artistid:String):Call<BaseResponse<Artist>>

    @GET
    fun getLyrics(@Url url:String, @Query("musicId") musicId:String): BaseResponseX<Lyrics>
    // "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=228908&httpsStatus=1&reqId=f9204c10-1df1-11ec-8b4f-9f163660962a"

    @GET
    fun getPlayUrl(@Url url:String):Call<ResponseBody>
}