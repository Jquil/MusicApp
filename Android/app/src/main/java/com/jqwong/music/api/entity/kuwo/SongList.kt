package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
@JsonClass(generateAdapter = true)
data class SongList(
    @field:Json(name = "img") val img:String,
    @field:Json(name = "num") val num:String,
    @field:Json(name = "pub") val pub:String,
    @field:Json(name = "musicList") val musicList:List<Song>
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