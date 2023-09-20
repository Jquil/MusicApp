package com.jqwong.music.helper

import com.jqwong.music.app.App
import com.jqwong.music.model.Alias
import com.jqwong.music.model.ExceptionLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class RequestHelper {

    companion object{
        fun getAlias(callback:(response:com.jqwong.music.model.Response<List<Alias>>) -> Unit){
            val title = this::getAlias.name
            CoroutineScope(Dispatchers.IO).launch {
                val url = "https://jquil.github.io/api/music/alias.json"
                val client = OkHttpClient.Builder()
                    .connectTimeout(App.config.okhttp_request_timeout,TimeUnit.MILLISECONDS)
                    .build()
                val request = Request.Builder()
                    .url(url)
                    .build()
                client.newCall(request).enqueue(object:Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        callback(com.jqwong.music.model.Response(
                            title = title,
                            data = null,
                            message = "",
                            success = false,
                            support = true,
                            exception = ExceptionLog(
                                title = title,
                                exception = e,
                                time = TimeHelper.getTime()
                            )
                        ))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val content = response.body()!!.string()
                            val data = Alias.fromJson(content)
                            callback(com.jqwong.music.model.Response(
                                title = title,
                                support = true,
                                success = true,
                                data = data,
                                exception = null,
                                message = ""
                            ))
                        }
                        catch (e:Exception){
                            callback(com.jqwong.music.model.Response(
                                title = title,
                                support = true,
                                success = false,
                                data = null,
                                exception = ExceptionLog(
                                    title = title,
                                    exception = e,
                                    time = TimeHelper.getTime()
                                ),
                                message = ""
                            ))
                        }
                    }

                })
            }
        }
    }
}