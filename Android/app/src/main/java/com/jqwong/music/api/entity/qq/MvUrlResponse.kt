package com.jqwong.music.api.entity.qq

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MvUrlResponse(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "getMvUrl") val getMvUrl:GetMvUrl
)

@JsonClass(generateAdapter = true)
data class GetMvUrl(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "data") val data:Any
)