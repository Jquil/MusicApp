package com.jqwong.music.api.entity.qq

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Album(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "mid") val mid:String,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "pmid") val pmid:String,
    @field:Json(name = "title") val title:String,
)