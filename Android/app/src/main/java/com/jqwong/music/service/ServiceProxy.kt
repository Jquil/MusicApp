package com.jqwong.music.service

import com.jqwong.music.helper.FunHelper
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.model.*

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class ServiceProxy {
    companion object{
        private val services = mapOf(
            Platform.KuWo to KuWOService(),
            Platform.NetEaseCloud to NetEaseCloudService()
        )
        suspend fun search(platform:Platform, key:String, page:Int, limit:Int): Response<List<Media>> {
            if(!services.containsKey(platform)){
                return notSupportPlatform(FunHelper.getName(),platform)
            }
            return services.get(platform)!!.search(key, page, limit)
        }
        suspend fun getLeaderboard(platform:Platform): Response<List<Leaderboard>> {
            if(!services.containsKey(platform)){
                return notSupportPlatform(FunHelper.getName(),platform)
            }
            return services.get(platform)!!.getLeaderboard()
        }
        suspend fun getLeaderboardSongList(platform:Platform, id:String, page:Int, limit:Int): Response<List<Media>> {
            if(!services.containsKey(platform)){
                return notSupportPlatform(FunHelper.getName(),platform)
            }
            return services.get(platform)!!.getLeaderboardSongList(id, page, limit)
        }
        suspend fun getPlayUrl(platform:Platform,id:String,quality:Any):Response<String>{
            if(!services.containsKey(platform)){
                return notSupportPlatform(FunHelper.getName(),platform)
            }
            return services.get(platform)!!.getPlayUrl(id,quality)
        }
        suspend fun getLyrics(platform: Platform,id: String):Response<Lyrics>{
            if(!services.containsKey(platform)){
                return notSupportPlatform(FunHelper.getName(),platform)
            }
            return services.get(platform)!!.getLyrics(id)
        }
        private fun <T>notSupportPlatform(title:String,platform: Platform):Response<T>{
            return Response(
                title = title,
                success = false,
                data = null,
                message = "",
                exception = ExceptionLog(
                    title = title,
                    exception = Exception("not support '${platform.name}'"),
                    time = TimeHelper.getTime()
                ),
                support = false
            )
        }
    }
}