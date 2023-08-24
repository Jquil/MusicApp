package com.jqwong.music.model

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.jqwong.music.app.Constant
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * @author: Jq
 * @date: 7/28/2023
 */



class Config(
    var okhttp_request_timeout:Long,
    var ffmpeg_parse_timeout:Long,
    var data_sync_platform:Platform,
    var default_search_platform:Platform,
    var auto_change_platform:Boolean,
    var priority_auto_change_platform:Platform,
    var ffmpeg_parse_quality_audition:FmgQuality,
    var ffmpeg_parse_quality_upload:FmgQuality,
    var netEaseCloudMusicConfig: NetEaseCloudMusicConfig,
    var qqMusicConfig: QQMusicConfig,
    var kuGouMusicConfig: KuGouMusicConfig,
    var kuWoMusicConfig: KuWoMusicConfig,
    var memFireDbConfig: MemFireDbConfig
){
    companion object{
        fun default():Config{
            return Config(
                okhttp_request_timeout = 3000,
                ffmpeg_parse_timeout = 3000,
                data_sync_platform = Platform.NetEaseCloud,
                default_search_platform = Platform.KuWo,
                auto_change_platform = true,
                priority_auto_change_platform = Platform.KuWo,
                ffmpeg_parse_quality_audition = FmgQuality.HQ,
                ffmpeg_parse_quality_upload = FmgQuality.SQ,
                netEaseCloudMusicConfig = NetEaseCloudMusicConfig(
                    csrf_token = "",
                    music_a = "",
                    quality = NetEaseCloudMusicConfig.qualities.get("无损")!!,
                    uid = "",
                    name = ""
                ),
                qqMusicConfig = QQMusicConfig(
                    cookies = HashMap<String,String>()
                ),
                kuGouMusicConfig = KuGouMusicConfig(
                    cookies = HashMap<String,String>()
                ),
                kuWoMusicConfig = KuWoMusicConfig(
                    cookies = HashMap<String,String>(),
                    quality = KuWoMusicConfig.qualities.getOrDefault("flac","flac")
                ),
                memFireDbConfig = MemFireDbConfig(
                    url = "",
                    apiKey = "",
                    user = null,
                    upload_favorite_artist = true,
                    upload_exception = true,
                    upload_video_bind_lyric_info = true
                )
            )
        }
        fun fromJson(json:String):Config{
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
                .build()
            val adapter = moshi.adapter(Config::class.java)
            return adapter.fromJson(json)!!
        }
    }
    fun save(ctx: Context){
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(Config::class.java)
        val sp = ctx.getSharedPreferences(Constant.CONFIG, AppCompatActivity.MODE_PRIVATE)
        with(sp.edit()){
            putString(com.jqwong.music.app.Constant.CONFIG,adapter.toJson(this@Config))
            apply()
        }
    }
}

class NetEaseCloudMusicConfig(
    var uid:String?,
    var name:String?,
    var csrf_token:String?,
    var music_a:String?,
    var quality:String
)
{
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

class QQMusicConfig(
    var cookies:Map<String,String>,
)

class KuGouMusicConfig(
    var cookies:Map<String,String>
)

class KuWoMusicConfig(
    var cookies:MutableMap<String,String>,
    var quality: String
)
{
    companion object{
        val qualities = mapOf(
            "mp3" to "mp3",
            "flac" to "flac"
        )
    }
}

class MemFireDbConfig(
    var url:String,
    var apiKey:String,
    var user:User?,
    var upload_favorite_artist:Boolean,
    var upload_video_bind_lyric_info:Boolean,
    var upload_exception:Boolean
)
{
    class User(
        var id:String,
        var name:String,
        var password:String
    )
}