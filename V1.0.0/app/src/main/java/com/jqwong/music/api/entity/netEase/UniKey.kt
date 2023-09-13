package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UniKey(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "unikey") val unikey:String
)