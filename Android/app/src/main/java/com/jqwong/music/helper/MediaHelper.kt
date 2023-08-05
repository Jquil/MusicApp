package com.jqwong.music.helper

/**
 * @author: Jq
 * @date: 8/4/2023
 */

fun String.toKwTime():Long{
    return (toDouble()*1000).toLong()
}