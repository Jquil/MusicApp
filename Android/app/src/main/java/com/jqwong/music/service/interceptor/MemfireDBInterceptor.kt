package com.jqwong.music.service.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class MemfireDBInterceptor():Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val builder = req.newBuilder()
            .header("apikey","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoic2VydmljZV9yb2xlIiwiZXhwIjozMjEyNjQwMjI2LCJpYXQiOjE2NzQ3MjAyMjYsImlzcyI6InN1cGFiYXNlIn0.808ob57XAZ4tpDL8Ynxvway2ufwzwnBMDhPW9IS8-Ew")
        return chain.proceed(builder.build())
    }
}