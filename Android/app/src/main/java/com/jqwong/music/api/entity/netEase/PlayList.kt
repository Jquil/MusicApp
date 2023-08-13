package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayListDetail(
    //@field:Json(name = "tracks") val tracks:List<Song>,
    @field:Json(name = "trackIds") val trackIds:List<TrackId>
)


@JsonClass(generateAdapter = true)
data class TrackId(
    @field:Json(name = "id") val id:Long
)