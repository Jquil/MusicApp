package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 8/13/2023
 */
@JsonClass(generateAdapter = true)
data class UserSheet(
    @field:Json(name = "id")  val id:Long,
    @field:Json(name = "name")  val name:String,
    @field:Json(name = "coverImgUrl")  val coverImgUrl:String,
)