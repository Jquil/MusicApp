package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
@JsonClass(generateAdapter = true)
data class Artist(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "info") val info:String,
    @field:Json(name = "pic") val pic:String,
)