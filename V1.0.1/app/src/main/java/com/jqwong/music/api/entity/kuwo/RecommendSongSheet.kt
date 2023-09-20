package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
@JsonClass(generateAdapter = true)
data class RecommendSongSheet(
    @field:Json(name = "list") val list:List<Item>
)
{
    @JsonClass(generateAdapter = true)
    data class Item(
        @field:Json(name = "img") val img:String,
        @field:Json(name = "uname") val uname:String,
        @field:Json(name = "img700") val img700:String,
        @field:Json(name = "name") val name:String,
        @field:Json(name = "id") val id:Long,
        @field:Json(name = "info") val info:String
    )
}