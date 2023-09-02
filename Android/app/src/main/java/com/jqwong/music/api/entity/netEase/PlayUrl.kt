package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PlayUrl(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "url") val url:String,
    @field:Json(name = "time") val time:Long
)