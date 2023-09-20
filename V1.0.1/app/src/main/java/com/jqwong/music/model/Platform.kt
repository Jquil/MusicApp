package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
enum class Platform {
    NetEaseCloud(){
        override fun toString(): String {
            return "网易云音乐"
        }
    },
    QQ(){
        override fun toString(): String {
            return "QQ音乐"
        }
    },
    KuWo(){
        override fun toString(): String {
            return "酷我音乐"
        }
    };

    companion object{
        private val map = mutableMapOf<String,Platform>()
        private fun initMap(){
            map.clear()
            Platform.values().forEach {
                map.put(it.toString(),it)
            }
        }
        fun get(name:String):Platform{
            if(map.isEmpty())
                initMap()
            return map.get(name)!!
        }
    }
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