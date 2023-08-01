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