package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Response<T>(
    val title:String,
    val success:Boolean,
    val message:String,
    val data:T?,
    val exception:ExceptionLog?,
    val support:Boolean
)