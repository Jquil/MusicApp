package com.jqwong.music.api.service.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class RemoteApiServiceInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req = chain.request()
        var builder = req.newBuilder()
            .header("apikey","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoic2VydmljZV9yb2xlIiwiZXhwIjozMjEyNjQwMjI2LCJpYXQiOjE2NzQ3MjAyMjYsImlzcyI6InN1cGFiYXNlIn0.808ob57XAZ4tpDL8Ynxvway2ufwzwnBMDhPW9IS8-Ew")
        return chain.proceed(builder.build())
    }
}