package com.jqwong.music.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

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
