package com.jqwong.music.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class DateTimeUtil {

    companion object{

        fun getDate():String{
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(Calendar.getInstance().time)
        }

        fun minutesToTime(str:String):Long{
            var time:Long = 0
            if(str == "")
                return time
            val splitTimeArr = str.split(":")
            when(splitTimeArr.size)
            {
                1 -> {
                    time= splitTimeArr[0].toLong()
                }
                2 -> {
                    time = splitTimeArr[0].toLong() * 60 + splitTimeArr[1].toLong()
                }
                3 -> {
                    time = splitTimeArr[0].toLong() * 60 * 60 + splitTimeArr[1].toLong() * 60 + splitTimeArr[2].toLong()
                }
            }
            return time * 1000
        }
    }
}