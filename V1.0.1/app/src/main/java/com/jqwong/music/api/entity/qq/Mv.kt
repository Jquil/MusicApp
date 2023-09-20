package com.jqwong.music.api.entity.qq

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Mv(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "vid") val vid:String,
)