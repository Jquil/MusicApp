package com.jqwong.music.helper

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.jqwong.music.app.App
import com.jqwong.music.model.ExceptionLog
import com.jqwong.music.model.Version
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException

class UpdateHelper {
    // https://blog.csdn.net/ahuyangdong/article/details/119456466
    companion object{
        fun newest(callback:(response:com.jqwong.music.model.Response<Version>) -> Unit){
            val title = this::newest.name
            CoroutineScope(Dispatchers.IO).launch {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://jquil.github.io/api/music/version.json")
                    .build()
                client.newCall(request).enqueue(object:Callback{
                    override fun onFailure(call: Call, e: IOException) {
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

                    override fun onResponse(call: Call, response: Response) {
                        val content = response.body().toString()
                        try {
                            val data = Version.fromJson(content)
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
        fun install(path:String){
            val file = File(path)
            if(!file.exists())
                return
            val intent = Intent(Intent.ACTION_VIEW)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val uri = FileProvider.getUriForFile(App.ctx!!,"${App.ctx!!.packageName}.fileprovider",file)
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
            }
            else{
                intent.setDataAndType(Uri.parse("file://$path"),"application/vnd.android.package-archive")
            }
            App.ctx!!.startActivity(intent)
        }
    }
}