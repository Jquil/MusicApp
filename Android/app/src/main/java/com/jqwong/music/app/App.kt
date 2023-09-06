package com.jqwong.music.app

import android.app.Application
import androidx.media3.common.util.UnstableApi
import com.jqwong.music.helper.AudioHelper
import com.jqwong.music.model.*


/**
 * @author: Jq
 * @date: 7/29/2023
 */
class App:Application(){
    companion object{
        lateinit var config:Config
        val userSheets:MutableMap<Platform,List<SongSheet>> = mutableMapOf()
        var exceptions:MutableList<ExceptionLog> = mutableListOf()
        val version = Version("1.0.0",1)
        lateinit var playList:PlayList

        fun playListIsInitialized():Boolean{
            return this::playList.isInitialized
        }
    }

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        AudioHelper.init(this)
    }
}