package com.jqwong.music.helper

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.jqwong.music.app.App
import java.io.File

class CacheHelper {
    companion object{
        fun getTotalCacheSize(ctx:Context):Long{
            return getFolderSize(ctx.cacheDir)
        }

        fun clear(ctx: Context){
            val ignore = mutableListOf<String>()
            if(App.playListIsInitialized() && App.playList.data.isNotEmpty()){
                val media = App.playList.data[App.playList.index]
                if(media.is_local){
                    ignore.add(media.filename())
                }
            }

            ctx.cacheDir.listFiles()?.forEach {
                if(it.isDirectory){
                    deleteDirectory(it,ignore)
                } else{
                    if(!ignore.contains(it.name)){
                        it.delete()
                    }
                }
            }
        }

        private fun getFolderSize(file:File):Long{
            var size:Long = 0
            file.listFiles()?.forEach {
                if(it.isDirectory){
                    size += getFolderSize(it)
                } else{
                    size += it.length()
                }
            }
            return size
        }

        private fun deleteDirectory(file:File, ignore:List<String>){
            file.listFiles()?.forEach {
                if(!ignore.contains(it.name)){
                    it.delete()
                }
            }
        }
    }
}