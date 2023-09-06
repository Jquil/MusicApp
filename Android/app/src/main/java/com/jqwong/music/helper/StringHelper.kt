package com.jqwong.music.helper



fun String.toKwTime():Long{
    return (toDouble()*1000).toLong()
}

fun String.toNetEaseCloudTime():Long{
    val arr = split(":")
    val first = arr[0].toLong() * 60
    return ((arr[1].toDouble() + first) * 1000).toLong()
}