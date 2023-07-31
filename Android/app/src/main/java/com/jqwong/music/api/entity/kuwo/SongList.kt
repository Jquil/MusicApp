package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class SongList(
    val img:String,
    val num:String,
    val pub:String,
    val musicList:List<Song>
)

@JsonClass(generateAdapter = true)
data class SongListX(
    @field:Json(name = "musicList") var musicList:List<Song>
)

@JsonClass(generateAdapter = true)
data class SongListM(
    @field:Json(name = "total") val total:Int,
    @field:Json(name = "list") val list:List<Song>
)