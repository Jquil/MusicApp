package com.jqwong.music.view.web

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class QQWebViewClient:WebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        request?.url?.toString().let {
            if(!it.isNullOrEmpty() && it.contains("sign=")){

            }
        }
        return super.shouldInterceptRequest(view, request)
    }
}