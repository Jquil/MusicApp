package com.jqwong.music.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author: Jq
 * @date: 5/1/2023
 */
class CommonUtil {

    companion object{

        inline fun <reified T>getName():String{
            return T::class.java.simpleName
        }
    }
}