package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "msg") val msg:String,
    @field:Json(name = "data") val data:T,
    @field:Json(name = "profileId") val profileId:String,
    @field:Json(name = "curTime") val curTime:Long,
    //@field:Json(name = "success") val success:Boolean
)


class BaseResponseX<T>(
    val msg:String,
    val profileid:String,
    val reqid:String,
    val status:Int,
    var data:T
)