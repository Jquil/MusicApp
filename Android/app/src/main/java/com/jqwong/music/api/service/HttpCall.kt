package com.jqwong.music.api.service

interface HttpCall<T> {

    fun onError(e:Throwable)

    fun OnSuccess(t:T)
}