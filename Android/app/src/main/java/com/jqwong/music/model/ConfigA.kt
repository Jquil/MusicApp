package com.jqwong.music.model

/**
 * okhttp_request_timeout:Long
 * 网络请求超时时间
 * default_search_platform:Platform
 * 默认搜索平台
 * retry_max_count
 * 重试最大次数
 * exit_clear_cache:Boolean
 * 退出后自动清除缓存
 * allow_auto_change_platform
 * 允许自动换源
 * change_platform_priority:List<Platform>
 * 换源优先级
 * allow_use_ffmpeg_parse
 * 允许使用ffmpeg解析
 * only_wifi_use_ffmpeg_parse:Boolean
 * 只允许在wifi状态下使用ffmpeg解析音频
 * ----------------------------------
 * 网易云配置
 * netEaseCloudMusicConfig:{
 *   sync_user_sheet:Boolean
 *   uid,
 *   name,
 *   csrf_token,
 *   music_a,
 *   quality,
 *   cookie,
 * }
 * ----------------------------------
 * 酷我配置
 * kuWoConfig:{
 *   quality,
 *   cookie,
 * }
 * ----------------------------------
 */
class ConfigA(
    okhttp_request_timeout:Long,
    default_search_platform:Platform,
    retry_max_count:Int,
    exit_clear_cache:Boolean,
    allow_auto_change_platform:Boolean,
    change_platform_priority:MutableList<Platform>,
    allow_use_ffmpeg_parse:Boolean,
    only_wifi_use_ffmpeg_parse:Boolean,
    netEaseCloudConfig: NetEaseCloudConfig,
    kuWoConfig: KuWoConfig,
) {
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
}