package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CheckLoginResponse(
    @field:Json(name = "message") val message:String,
    @field:Json(name = "code") val code:Int,
)