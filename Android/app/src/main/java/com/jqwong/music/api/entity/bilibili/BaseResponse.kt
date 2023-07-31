package com.jqwong.music.api.entity.bilibili

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class BaseResponse<T>(
    val code:Int,
    val message:String,
    val ttl:Int,
    val data:T
)