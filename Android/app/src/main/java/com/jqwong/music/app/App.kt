package com.jqwong.music.app

import android.app.Application
import com.jqwong.music.model.Config
import com.jqwong.music.model.ExceptionLog
import com.jqwong.music.model.Platform

/**
 * @author: Jq
 * @date: 7/29/2023
 */
class App:Application(){
    companion object{
        lateinit var config:Config
        var exceptions:MutableList<ExceptionLog> = mutableListOf()
        var enable_platform = listOf(Platform.KuWo)
    }
}