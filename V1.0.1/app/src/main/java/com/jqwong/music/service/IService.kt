package com.jqwong.music.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.model.*

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface IService {
    fun getPlatform():Platform
    suspend fun collectOrCancelSong(collect:Boolean,data: Any):Response<Boolean>
    suspend fun getLeaderboard():Response<List<Leaderboard>>
    suspend fun getLeaderboardSongList(id:String, page:Int, limit:Int):Response<List<Media>>
    suspend fun getArtistSongList(id:String, page:Int, limit:Int):Response<List<Media>>
    suspend fun getArtistInfo(id:Long):Response<Artist>
    suspend fun search(key:String, page:Int, limit:Int):Response<List<Media>>
    suspend fun getRecommendSongSheetList(data:Any):Response<List<SongSheet>>
    suspend fun getRecommendSongSheetData(id:String, page:Int, limit:Int,data:Any):Response<List<Media>>
    suspend fun getRecommendDaily(data:Any):Response<List<Media>>
    suspend fun getPlayUrl(id:String, quality:Any):Response<String>
    suspend fun getMvUrl(id:String):Response<String>
    suspend fun getLyrics(id:String):Response<Lyrics>
    suspend fun getUserSheet(data: Any):Response<List<SongSheet>>
    suspend fun getUserSheetData(page:Int, limit:Int,data:Any):Response<List<Media>>

    fun <T>error(title:String, e:Exception):Response<T>{
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