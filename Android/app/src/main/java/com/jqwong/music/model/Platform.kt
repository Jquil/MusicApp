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
    OnlyFromPlayUrl(){
        override fun toString(): String {
            return "Url"
        }
    },
    OnlyFromParseMv(){
        override fun toString(): String {
            return "MV"
        }
    },
    AllOfTheAbove(){
        override fun toString(): String {
            return "以上都使用"
        }
    }
}

class ChangePlatformItem(
    var index:Int,
    var platform: Platform,
    var enable:Boolean,
    var mode: ChangePlatformMode
)