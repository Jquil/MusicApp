package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Artist(
    val id:String,
    val name:String,
    val pic:String?,
    val description:String?,
    val platform: Platform
)

fun List<Artist>.toName():String{
    val builder = StringBuilder()
    forEach {
        builder.append(it.name)
        builder.append('/')
    }
    builder.deleteCharAt(builder.length-1)
    return builder.toString()
}