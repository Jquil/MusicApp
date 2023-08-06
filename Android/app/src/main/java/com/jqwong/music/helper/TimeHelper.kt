package com.jqwong.music.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

/**
 * @author: Jq
 * @date: 7/29/2023
 */
class TimeHelper {

    companion object
    {
        @RequiresApi(Build.VERSION_CODES.O)
        fun getTime():Long{
            return Instant.now().epochSecond
        }
    }
}
fun String.toKwTime():Long{
    return (toDouble()*1000).toLong()
}

fun String.toNetEaseCloudTime():Long{
    val arr = split(":")
    val first = arr[0].toLong() * 60
    return ((arr[1].toDouble() + first) * 1000).toLong()
}