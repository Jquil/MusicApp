package com.jqwong.music.utils

import java.util.*

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class IDUtil {
    companion object{
        fun newUUID():String{
            return UUID.randomUUID().toString()
        }
    }
}