package com.jqwong.music.helper

/**
 * @author: Jq
 * @date: 7/29/2023
 */
class TimeHelper {

    companion object
    {
        fun getTime():Long{
            return System.currentTimeMillis()/1000
        }
    }
}
