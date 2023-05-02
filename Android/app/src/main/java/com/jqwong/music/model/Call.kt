package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 4/29/2023
 */
interface Call<T> {
    fun success(data:T);
    fun error(e:Throwable)
}