package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 8/13/2023
 */

@JsonClass(generateAdapter = true)
data class UserResponse(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "profile") val profile: Profile
)


@JsonClass(generateAdapter = true)
data class Profile(
    @field:Json(name = "userId") val userId:Long,
    @field:Json(name = "nickname") val nickname:String,
)