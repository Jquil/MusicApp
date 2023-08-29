package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
enum class Platform {
    NetEaseCloud,
    QQ,
    KuWo,
}

enum class ChangePlatformMode{
    OnlyFromPlayUrl,
    OnlyFromParseMv,
    AllOfTheAbove
}

class ChangePlatformItem(
    val index:Int,
    val platform: Platform,
    val enable:Boolean,
    val mode: ChangePlatformMode
)