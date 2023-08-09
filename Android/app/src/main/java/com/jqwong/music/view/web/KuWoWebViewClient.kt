package com.jqwong.music.view.web

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jqwong.music.app.App

class KuWoWebViewClient: WebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url?.toString()
        if(url != null && url.contains("/api/www/") && App.config.kuWoMusicConfig.cookies.isNullOrEmpty()){
            request.requestHeaders?.forEach {
                App.config.kuWoMusicConfig.cookies.put(it.key,it.value)
            }
            val cm = CookieManager.getInstance()
            val cookie = cm.getCookie("http://kuwo.cn")
            App.config.kuWoMusicConfig.cookies.put("Cookie",cookie)
        }
        return super.shouldInterceptRequest(view, request)
    }
}