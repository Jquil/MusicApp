package com.jqwong.music.helper

import android.content.Context
import com.jqwong.music.app.App
import com.jqwong.music.event.MediaLoadingEvent
import com.jqwong.music.model.Platform
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class AudioHelper {
    companion object{

        private lateinit var _ctx: Context

        fun init(ctx:Context){
            _ctx = ctx

        }

        fun start(){
            if(App.playListIsInitialized()){
                val media = App.playList.data.get(App.playList.index)
                if(media.audio != null){
                    if(media.audio!!.play_url == null){
                        EventBus.getDefault().post(MediaLoadingEvent(finish = false))
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = ServiceProxy.getPlayUrl(media.audio!!.platform,media.audio!!.id,
                                getPlayQuality(media.audio!!.platform)
                            )
                            if(result.exception != null){
                                App.exceptions.add(result.exception)
                            }
                            else{
                                media.audio!!.play_url = result.data
                                withContext(Dispatchers.Main){
                                    EventBus.getDefault().post(MediaLoadingEvent(false))
                                }
                            }
                        }
                    }
                }
            }
        }
        fun prev(){}
        fun next(){}
        fun play(){}
        fun pause(){}
        private fun getPlayQuality(platform: Platform):Any{
            when(platform){
                Platform.KuWo-> return "mp3"
                Platform.NetEaseCloud->return App.config.netEaseCloudMusicConfig.quality
                Platform.KuGou-> return ""
                else -> return ""
            }
        }
    }
}