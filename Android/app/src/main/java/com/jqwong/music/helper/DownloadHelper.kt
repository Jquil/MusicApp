package com.jqwong.music.helper

import com.jqwong.music.event.DownloadEvent
import com.jqwong.music.model.DownloadStatus
import com.jqwong.music.model.DownloadTask
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadHelper {
    companion object{
        private val taskList = mutableMapOf<String,DownloadTask>()
        fun add(task:DownloadTask){
            if(taskList.containsKey(task.id) && !taskList[task.id]!!.finish)
                return
            taskList.put(task.id,task)
            task.client = OkHttpClient()
            val request = Request.Builder()
                .url(task.downloadPath)
                .build()
            OkHttpClient().newCall(request).enqueue(object:Callback{
                override fun onFailure(call: Call, e: IOException) {
                    EventBus.getDefault().post(DownloadEvent(task.info().apply {
                        this.exception = e
                        this.status = DownloadStatus.Error
                    }))
                }

                override fun onResponse(call: Call, response: Response) {
                    val file = File(task.savePath)
                    if(file.exists())
                        file.delete()
                    var sum = 0
                    val _is = response.body()!!.byteStream()
                    val total = response.body()!!.contentLength()
                    val buffer = ByteArray(1024 * 2)
                    val fos = FileOutputStream(file)
                    var len = 0
                    while (_is.read(buffer).also { len = it } != -1){
                        fos.write(buffer,0,len)
                        sum += len
                        val progress = (sum * 1.0 / total * 100).toInt()
                        EventBus.getDefault().post(task.info().apply {
                            this.progress = progress
                            this.status = DownloadStatus.Downloading
                        })
                    }
                    EventBus.getDefault().post(task.info().apply {
                        this.status = DownloadStatus.Success
                    })
                    task.finish = true
                    task.callback()
                    fos.flush()
                    _is.close()
                    fos.close()
                }
            })
        }
        fun exist(id:String):Boolean{
            return taskList.containsKey(id)
        }
        fun downloading(id: String):Boolean{
            if(!taskList.containsKey(id)){
                return false
            }
            return taskList[id]!!.finish
        }
    }
}