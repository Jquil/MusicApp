package com.jqwong.music.api

import com.jqwong.music.model.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author: Jq
 * @date: 4/29/2023
 */
interface KuWo {

    @GET("search/searchKey?key=&httpsStatus=1&reqId=db3f8670-e1f6-11eb-942d-33e288737b1d")
    fun getHotSearch():Observable<BaseMusicData<List<String>>>

    @GET("search/searchMusicBykeyWord?httpsStatus=1&reqId=23016430-e1eb-11eb-a2ee-bf024dbfa4c7")
    fun getSearchResult(@Query("key") key:String, @Query("pn") page:Int, @Query("rn") size:Int):Observable<BaseMusicData<MediaList>>

    @GET("artist/artistMusic?httpsStatus=1&reqId=87263830-f72d-11eb-979c-c11891b4f2ba")
    fun getArtistMusicList(@Query("artistid") artistId:String,@Query("pn") page:Int, @Query("rn") size:Int):Observable<BaseMusicData<MediaList>>

    @GET("artist/artist?httpsStatus=1&reqId=b06e62f0-f582-11eb-bd8d-c19fac490f25")
    fun getArtistInfo(@Query("artistid") artistId:String):Observable<BaseMusicData<ArtistInfo>>

    @GET("bang/bang/bangMenu?httpsStatus=1&reqId=e617e730-e1f7-11eb-942d-33e288737b1d")
    fun getBillboardList():Observable<BaseMusicData<List<BillboardList>>>

    @GET("bang/bang/musicList?httpsStatus=1&reqId=b092ca30-e152-11eb-90cc-79484e9fbe4d")
    fun getBillboardMusicList(@Query("bangId") sourceId:String, @Query("pn") page:Int, @Query("rn") size:Int):Observable<BaseMusicData<BillboardMediaList>>
}