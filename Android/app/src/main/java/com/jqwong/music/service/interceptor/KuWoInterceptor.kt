package com.jqwong.music.service.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class KuWoInterceptor():Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        val builder = req.newBuilder()
            .header("COOKIE","_ga=GA1.2.291993560.1625886876; _gid=GA1.2.445012141.1625886876; Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1625903943,1625967386; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1625967696; kw_token=IP4FK471YEF")
            .header("CSRF","IP4FK471YEF")
            .header("REFERER","https://www.kuwo.cn/")

        return chain.proceed(builder.build())
    }
}