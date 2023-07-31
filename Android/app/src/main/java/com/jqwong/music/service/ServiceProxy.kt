package com.jqwong.music.service

import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.model.*

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class ServiceProxy {
    companion object{
        private val services = mapOf(
            Platform.KuWo to KuWOService()
        )

        suspend fun Search(platform:Platform, key:String,page:Int,limit:Int): Response<List<Media>> {
            val title = "Search"
            if(!services.containsKey(platform)){
                return notSupportPlatform(title,platform)
            }
            return services.get(platform)!!.Search(key, page, limit)
        }
        private fun <T>notSupportPlatform(title:String,platform: Platform):Response<T>{
            return Response(
                title = title,
                success = false,
                data = null,
                message = "failed",
                exception = ExceptionLog(
                    title = title,
                    message = "not support '${platform.name}'",
                    time = TimeHelper.getTime()
                )
            )
        }
    }


}