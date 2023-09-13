package com.jqwong.music.api.entity.qq

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistSongListResponse(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "singer") val singer:SingerSongList,
)

@JsonClass(generateAdapter = true)
data class SingerSongList(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "data") val data:ArtistSongListData
)

@JsonClass(generateAdapter = true)
data class ArtistSongListData(
    @field:Json(name = "songlist") val songlist:List<Song>,
)