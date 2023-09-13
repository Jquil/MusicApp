package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Leaderboard(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "description") val description:String?,
    @field:Json(name = "coverImgUrl") val coverImgUrl:String
)