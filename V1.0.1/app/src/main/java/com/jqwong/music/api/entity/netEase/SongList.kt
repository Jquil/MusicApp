package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongList(
    @field:Json(name = "songCount") val songCount:Int,
    @field:Json(name = "songs") val songs:List<Song>
)