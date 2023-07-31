package com.jqwong.music.helper

import java.time.Instant

/**
 * @author: Jq
 * @date: 7/29/2023
 */
class TimeHelper {

    companion object
    {
        fun getTime():Long{
            return Instant.now().epochSecond
        }
    }
}