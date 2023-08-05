package com.jqwong.music.helper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.jqwong.music.app.App
import com.jqwong.music.event.LyricsLoadingEvent
import com.jqwong.music.event.MediaChangeEvent
import com.jqwong.music.event.MediaLoadingEvent
import com.jqwong.music.event.MediaPositionChangeEvent
import com.jqwong.music.model.Lyrics
import com.jqwong.music.model.Platform
import com.jqwong.music.model.build
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
    @UnstableApi companion object{

        private val TAG = "AudioHelper"
        private val MAX_RELOAD_COUNT = 3
        private lateinit var _ctx: Context
        private lateinit var _session: MediaSession
        private lateinit var _player: Player
        private lateinit var _pListener:PositionListener
        private val _h = Handler()

        fun init(ctx:Context){
            _ctx = ctx
            _player = ExoPlayer.Builder(ctx)
                .setLooper(Looper.getMainLooper())
                .build()
            _session = MediaSession.Builder(ctx, _player).build()
            _player.playWhenReady = true
            _player.addListener(object: Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)){
                        if (_player.isPlaying){
                            _pListener = PositionListener()
                            _h.postDelayed(_pListener,100)
                        }
                        else{
                            if(this@Companion::_pListener.isInitialized)
                                _h.removeCallbacks(_pListener)
                        }
                    }
                }

            })
        }

        fun start(clear:Boolean = true){
            if(!App.playListIsInitialized())
                return
            if(clear){
                _player.stop()
                _player.clearMediaItems()
            }
            val media = App.playList.data.get(App.playList.index)
            EventBus.getDefault().post(MediaChangeEvent(media))
            if(media.audio != null){
                if(media.audio!!.play_url == null){
                    EventBus.getDefault().post(MediaLoadingEvent(finish = false))
                    getPlayUrl(media.audio!!.platform,media.audio!!.id, getPlayQuality(media.audio!!.platform),0) { success: Boolean, url: String ->
                        if (success) {
                            media.audio!!.play_url = url
                            App.playList.data.get(App.playList.index).audio!!.play_url = url
                            _player.addMediaItem(media.audio!!.build())
                            _player.prepare()
                            EventBus.getDefault().post(MediaLoadingEvent(finish = true))
                            EventBus.getDefault().post(LyricsLoadingEvent(false))
                            getLyrics(media.audio!!.platform,media.audio!!.id,0) { success, lyrics ->
                                if(success){
                                    App.playList.lyrics = lyrics
                                }
                                else{
                                    App.playList.lyrics = null
                                }
                                EventBus.getDefault().post(LyricsLoadingEvent(true))
                            }
                        } else {
                            App.playList.index++
                            start()
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
                Platform.KuWo-> return "flac"
                Platform.NetEaseCloud->return App.config.netEaseCloudMusicConfig.quality
                Platform.KuGou-> return ""
                else -> return ""
            }
        }
        private fun getLyrics(
            platform: Platform,
            id:String,
            reloadNumber:Int=0,
            call: (success:Boolean,lyrics:Lyrics?) -> Unit
        ){
            CoroutineScope(Dispatchers.IO).launch {
                val result = ServiceProxy.getLyrics(platform,id)
                withContext(Dispatchers.IO){
                    if(result.exception != null){
                        if(reloadNumber == MAX_RELOAD_COUNT){
                            Toast.makeText(_ctx,result.exception.exception.message.toString(),Toast.LENGTH_SHORT).show()
                            App.exceptions.add(result.exception)
                            call(false,null)
                        }
                        else{
                            getLyrics(platform,id,reloadNumber+1,call)
                        }
                    }
                    else{
                        call(true,result.data)
                    }
                }
            }
        }
        private fun getPlayUrl(
            platform: Platform,
            id:String,
            quality:Any,
            reloadNumber:Int=0,
            call: (success:Boolean,url:String) -> Unit
        ){
            CoroutineScope(Dispatchers.IO).launch {
                val result = ServiceProxy.getPlayUrl(platform,id,quality)
                withContext(Dispatchers.Main){
                    if(result.exception != null){
                        if(reloadNumber == MAX_RELOAD_COUNT){
                            Toast.makeText(_ctx,result.exception.exception.message.toString(),Toast.LENGTH_SHORT).show()
                            App.exceptions.add(result.exception)
                        }
                        else{
                            getPlayUrl(platform, id, quality, reloadNumber+1, call)
                        }
                    }
                    else{
                        call.invoke(true,result.data!!)
                    }
                }
            }
        }
    }

    class PositionListener : Runnable {
        override fun run() {
            EventBus.getDefault().post(MediaPositionChangeEvent(_player.currentPosition))
            _h.postDelayed(this, 100)
        }

    }
}