package com.jqwong.music.service

import com.jqwong.music.model.*

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface IService {
    fun GetLeaderboard():Response<Leaderboard>
    fun GetLeaderboardSongList(id:Long,page:Int,limit:Int):Response<List<Media>>
    fun GetArtistSongList(id:Long,page:Int,limit:Int)
    suspend fun Search(key:String,page:Int,limit:Int):Response<List<Media>>
    fun GetRecommendSongSheetList(data:Any):Response<RecommendSongSheet>
    fun GetRecommendSongSheetData(data:Any,page:Int,limit:Int):Response<List<Media>>
    fun GetRecommendDaily(data:Any):Response<List<Media>>
    fun GetPlayUrl(id:Long,quality:Any)
    fun GetMvUrl(id:Long)
    fun GetLyrics(id:Long)
}