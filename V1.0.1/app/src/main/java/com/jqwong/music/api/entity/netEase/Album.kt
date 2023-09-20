package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Album(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "picUrl") val picUrl:String,
)