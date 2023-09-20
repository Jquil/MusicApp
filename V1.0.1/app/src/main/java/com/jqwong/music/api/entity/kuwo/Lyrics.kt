package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Lyrics(
    @field:Json(name = "lrclist") val lrclist:List<Lyric>
)

@JsonClass(generateAdapter = true)
data class Lyric(
    @field:Json(name = "lineLyric") val lineLyric:String,
    @field:Json(name = "time") val time:String
)