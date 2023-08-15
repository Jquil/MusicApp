package com.jqwong.music.service

import android.os.Build
import androidx.annotation.RequiresApi
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

        fun all():Collection<IService>{
            return services.values
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getService(platform: Platform):Response<IService>{
            val title = this::getService.name
            return if(!services.containsKey(platform)){
                notSupportPlatform<IService>(title,platform)
            }
            else{
                Response(
                    title = title,
                    data = services.get(platform),
                    message = "ok",
                    success = true,
                    support = true,
                    exception = null
                )
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getRecommendDaily(platform:Platform, data: Any): Response<List<Media>> {
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getRecommendDaily.name,platform)
            }
            return services.get(platform)!!.getRecommendDaily(data)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun search(platform:Platform, key:String, page:Int, limit:Int): Response<List<Media>> {
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::search.name,platform)
            }
            return services.get(platform)!!.search(key, page, limit)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getLeaderboard(platform:Platform): Response<List<Leaderboard>> {
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getLeaderboard.name,platform)
            }
            return services.get(platform)!!.getLeaderboard()
        }
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getLeaderboardSongList(platform:Platform, id:String, page:Int, limit:Int): Response<List<Media>> {
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getLeaderboardSongList.name,platform)
            }
            return services.get(platform)!!.getLeaderboardSongList(id, page, limit)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getPlayUrl(platform:Platform, id:String, quality:Any):Response<String>{
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getPlayUrl.name,platform)
            }
            return services.get(platform)!!.getPlayUrl(id,quality)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getLyrics(platform: Platform, id: String):Response<Lyrics>{
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getLyrics.name,platform)
            }
            return services.get(platform)!!.getLyrics(id)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getRecommendSongSheetList(platform: Platform, data:Any):Response<List<SongSheet>>{
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getRecommendSongSheetList.name,platform)
            }
            return services.get(platform)!!.getRecommendSongSheetList(data)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getRecommendSongSheetData(platform:Platform,id:String, page:Int, limit:Int,data:Any,):Response<List<Media>>{
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getRecommendSongSheetData.name,platform)
            }
            return services.get(platform)!!.getRecommendSongSheetData(id,page,limit,data)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getUserSheetData(platform: Platform,page:Int,limit:Int, data:Any):Response<List<Media>>{
            if(!services.containsKey(platform)){
                return notSupportPlatform(this::getUserSheetData.name,platform)
            }
            return services.get(platform)!!.getUserSheetData(page,limit,data)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun <T>notSupportPlatform(title:String, platform: Platform):Response<T>{
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