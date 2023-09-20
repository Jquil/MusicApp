package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
@JsonClass(generateAdapter = true)
data class MvUrl(
    @field:Json(name = "url") val url:String
)