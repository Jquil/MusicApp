package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LyricResponse(
    @field:Json(name = "code")val code:Int,
    @field:Json(name = "lrc")val lrc:Lyric,
    @field:Json(name = "romalrc")val romalrc:RoMaLrc,
){
    @JsonClass(generateAdapter = true)
    data class Lyric(
        @field:Json(name = "version")val version:Int,
        @field:Json(name = "lyric")val lyric:String
    )
    @JsonClass(generateAdapter = true)
    data class RoMaLrc(
        @field:Json(name = "version")val version:Int,
        @field:Json(name = "lyric")val lyric:String
    )
}