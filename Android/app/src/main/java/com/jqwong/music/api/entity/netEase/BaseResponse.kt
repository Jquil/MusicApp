package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @field:Json(name = "result") val result:T,
    @field:Json(name = "code") val code:Int
)


@JsonClass(generateAdapter = true)
data class BaseResponseX<T>(
    @field:Json(name = "list") val list:T,
    @field:Json(name = "code") val code:Int
)

@JsonClass(generateAdapter = true)
data class BaseResponseZ<T>(
    @field:Json(name = "playlist") val playlist:T,
    @field:Json(name = "code") val code:Int,
)

@JsonClass(generateAdapter = true)
data class BaseResponseM<T>(
    @field:Json(name = "data") val data:T,
    @field:Json(name = "code") val code:Int,
)

@JsonClass(generateAdapter = true)
data class BaseResponseO<T>(
    @field:Json(name = "songs") val songs:T,
    @field:Json(name = "code") val code:Int,
)

