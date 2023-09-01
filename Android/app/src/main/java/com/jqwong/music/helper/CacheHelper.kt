package com.jqwong.music.helper

import android.content.Context
import java.io.File

class CacheHelper {
    companion object{
        fun getTotalCacheSize(ctx:Context):Long{
            return getFolderSize(ctx.cacheDir)
        }

        fun clear(ctx: Context){
            ctx.cacheDir.deleteRecursively()
        }

        private fun getFolderSize(file:File):Long{
            var size:Long = 0
            file.listFiles().forEach {
                if(it.isDirectory){
                    size += getFolderSize(it)
                }
                else{
                    size += it.length()
                }
            }
            return size
        }
    }
}