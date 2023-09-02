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
        @RequiresApi(Build.VERSION_CODES.O)
        fun get(platform: Platform):Response<IService>{
            val title = this::get.name
            return if(!services.containsKey(platform)){
                notSupportPlatform(title,platform)
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