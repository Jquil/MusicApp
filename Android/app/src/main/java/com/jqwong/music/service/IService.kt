package com.jqwong.music.service

import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.model.*

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface IService {
    suspend fun getLeaderboard():Response<List<Leaderboard>>
    suspend fun getLeaderboardSongList(id:String, page:Int, limit:Int):Response<List<Media>>
    suspend fun getArtistSongList(id:Long, page:Int, limit:Int):Response<List<Media>>
    suspend fun getArtistInfo(id:Long):Response<Artist>
    suspend fun search(key:String, page:Int, limit:Int):Response<List<Media>>
    suspend fun getRecommendSongSheetList(data:Any):Response<List<SongSheet>>
    suspend fun getRecommendSongSheetData(data:Any, page:Int, limit:Int):Response<List<Media>>
    suspend fun getRecommendDaily(data:Any):Response<List<Media>>
    suspend fun getPlayUrl(id:String, quality:Any):Response<String>
    suspend fun getMvUrl(id:String):Response<String>
    suspend fun getLyrics(id:String):Response<Lyrics>
    fun <T>error(title:String,e:Exception):Response<T>{
        return Response(
            title = title,
            success = false,
            message = e.message.toString(),
            data = null,
            exception = ExceptionLog(
                title = title,
                exception = e,
                time = TimeHelper.getTime()
            ),
            support = true
        )
    }
    fun <T>notSupport(title:String):Response<T>{
        return Response(
            title = title,
            success = false,
            message = "",
            data = null,
            exception = null,
            support = false
        )
    }
}