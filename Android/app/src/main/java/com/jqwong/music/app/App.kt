package com.jqwong.music.app

import android.app.Application
import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.jqwong.music.helper.AudioHelper
import com.jqwong.music.model.*


/**
 * @author: Jq
 * @date: 7/29/2023
 */
class App:Application(){
    companion object{
        var ctx:Context? = null
        lateinit var config: Config
        val userSheets:MutableMap<Platform,List<SongSheet>> = mutableMapOf()
        var exceptions:MutableList<ExceptionLog> = mutableListOf()
        val globalAlias = mutableMapOf<String,List<String>>()
        val version = Version("1.0.0",1,"初始化版本","")
        var newestVersion:Version? = null
        lateinit var playList:PlayList
        fun playListIsInitialized():Boolean{
            return this::playList.isInitialized
        }

    }

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        ctx = this
        AudioHelper.init(ctx!!)
        config = Config.default()
    }
}