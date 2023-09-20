package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/30/2023
 */
class ApiResult<T>(
    val data:T?,
    val e:Exception?,
    val header:okhttp3.Headers?
)