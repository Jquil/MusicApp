package com.jqwong.music.model

import okhttp3.OkHttpClient

class DownloadTask(
    val id:String,
    val name:String,
    val downloadPath:String,
    val savePath:String,
    var finish:Boolean,
    var client:OkHttpClient?,
    val callback:() -> Unit,
){
    fun info():DownloadInfo{
        return DownloadInfo(
            id,
            name,
            downloadPath,
            savePath,
            0,
            DownloadStatus.None,
            null
        )
    }
}

class DownloadInfo(
    val id:String,
    val name:String,
    val downloadPath:String,
    val savePath:String,
    var progress:Int,
    var status: DownloadStatus,
    var exception:Exception?,
)

enum class DownloadStatus{
    None,
    Downloading,
    Success,
    Error,
    Stop
}