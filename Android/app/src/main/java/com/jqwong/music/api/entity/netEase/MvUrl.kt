package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 8/13/2023
 */
@JsonClass(generateAdapter = true)
data class MvUrl(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "url")val url:String
)