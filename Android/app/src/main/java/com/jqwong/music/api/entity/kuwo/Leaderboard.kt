package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
@JsonClass(generateAdapter = true)
data class Leaderboard(
    @field:Json(name = "name") val name:String,
    @field:Json(name = "list") val list:List<Item>
){
    @JsonClass(generateAdapter = true)
    data class Item(
        @field:Json(name = "intro") val intro:String,
        @field:Json(name = "name") val name:String,
        @field:Json(name = "id") val id:String,
        @field:Json(name = "pic") val pic:String,
        @field:Json(name = "pub") val pub:String,
    )
}