package com.jqwong.music.model

import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.jqwong.music.R
import com.jqwong.music.app.App
import com.jqwong.music.app.Constant
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Config(
    var okhttp_request_timeout:Long,
    var default_search_platform:Platform,
    var retry_max_count:Int,
    var exit_clear_cache:Boolean,
    var allow_auto_change_platform:Boolean,
    var change_platform_priority:MutableList<ChangePlatformItem>,
    var allow_use_media_extractor_parse:Boolean,
    var only_wifi_use_media_extractor_parse:Boolean,
    var downloadPath:String,
    val data:MutableMap<String,Any>,
    var netEaseCloudConfig: NetEaseCloudConfig,
    var kuWoConfig: KuWoConfig,
    var qqConfig:QQConfig
) {
    fun save(ctx:Context){
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(Config::class.java)
        val json = adapter.toJson(this)
        val sp = ctx.getSharedPreferences(Constant.CONFIG, AppCompatActivity.MODE_PRIVATE)
        with(sp.edit()){
            putString(Constant.CONFIG,json)
            apply()
        }
    }
    class NetEaseCloudConfig(
        var sync_user_sheet:Boolean,
        var uid:String,
        var name:String,
        var csrf_token:String,
        var music_a:String,
        var quality:String,
        var cookie:MutableMap<String,String>
    ){
        companion object{
            val qualities = mapOf(
                "标准" to "standard",
                "较高" to "higher",
                "极高" to "exhigh",
                "无损" to "lossless",
                "Hi-Res" to "hires",
                "高清环绕声" to "jyeffect",
                "沉浸环绕声" to "sky",
                "超清母带" to "jymaster",
            )
        }
    }
    class KuWoConfig(
        var cookies:MutableMap<String,String>,
        var quality: String
    ){
        companion object{
            val qualities = mapOf(
                "mp3" to "mp3",
                "flac" to "flac"
            )
        }
    }
    class QQConfig(
        var quality: String
    ){
        companion object{
            val qualities = mapOf(
                "标准" to "mp3",
                "较高" to "hq",
                "Hi-Res" to "hr",
                "无损" to "sq",
            )
        }
    }

    companion object{
        fun default():Config{
            val config = Config(
                okhttp_request_timeout = 3000,
                default_search_platform = Platform.NetEaseCloud,
                retry_max_count = 3,
                exit_clear_cache = true,
                allow_auto_change_platform = true,
                change_platform_priority = mutableListOf(
                    ChangePlatformItem(0,Platform.NetEaseCloud,true,ChangePlatformMode.AllOfTheAbove),
                    ChangePlatformItem(1,Platform.KuWo,true,ChangePlatformMode.AllOfTheAbove),
                    ChangePlatformItem(2,Platform.QQ,true,ChangePlatformMode.AllOfTheAbove),
                ),
                allow_use_media_extractor_parse = true,
                only_wifi_use_media_extractor_parse = true,
                netEaseCloudConfig = NetEaseCloudConfig(
                    sync_user_sheet = true,
                    uid = "",
                    name = "",
                    csrf_token = "",
                    music_a = "",
                    quality = NetEaseCloudConfig.qualities["标准"]!!,
                    cookie = mutableMapOf()
                ),
                kuWoConfig = KuWoConfig(
                    cookies = mutableMapOf(),
                    quality = KuWoConfig.qualities["flac"]!!
                ),
                qqConfig = QQConfig(
                    quality = QQConfig.qualities["标准"]!!
                ),
                downloadPath = "",
                data = mutableMapOf()
            )
            var directory = "msc"
            if (App.ctx != null){
                directory = App.ctx!!.getString(R.string.app)
            }
            config.downloadPath = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_MUSIC}/${directory}"
            return config
        }
        fun fromJson(json:String):Config{
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(Config::class.java)
            return adapter.fromJson(json)!!
        }
    }
}