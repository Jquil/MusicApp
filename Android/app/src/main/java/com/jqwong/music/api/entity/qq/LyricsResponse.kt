package com.jqwong.music.api.entity.qq

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LyricsResponse(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "PlayLyricInfo") val PlayLyricInfo:PlayLyricInfo
)

@JsonClass(generateAdapter = true)
data class PlayLyricInfo(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "data") val data:LyricsData,
)

@JsonClass(generateAdapter = true)
data class LyricsData(
    @field:Json(name = "songID") val songID:Long,
    @field:Json(name = "lyric") val lyric:String,
)