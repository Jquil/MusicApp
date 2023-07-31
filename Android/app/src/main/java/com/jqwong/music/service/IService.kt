package com.jqwong.music.service

import com.jqwong.music.model.Leaderboard
import com.jqwong.music.model.RecommendSongSheet
import com.jqwong.music.model.Response
import com.jqwong.music.model.Song

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface IService {
    fun GetLeaderboard():Response<Leaderboard>
    fun GetLeaderboardSongList(id:Long,page:Int,limit:Int):Response<List<Song>>
    fun GetArtistSongList(id:Long,page:Int,limit:Int)
    suspend fun Search(key:String,page:Int,limit:Int):Response<List<Song>>
    fun GetRecommendSongSheetList(data:Any):Response<RecommendSongSheet>
    fun GetRecommendSongSheetData(data:Any,page:Int,limit:Int):Response<List<Song>>
    fun GetRecommendDaily(data:Any):Response<List<Song>>
    fun GetPlayUrl(id:Long,quality:Any)
    fun GetMvUrl(id:Long)
    fun GetLyrics(id:Long)
}