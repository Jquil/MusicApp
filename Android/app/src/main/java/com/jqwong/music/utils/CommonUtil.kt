package com.jqwong.music.utils

import android.util.Log

class CommonUtil {

    companion object{

        private val TAG = "CommonUtil"

        fun StrToTime(str:String):Long{
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