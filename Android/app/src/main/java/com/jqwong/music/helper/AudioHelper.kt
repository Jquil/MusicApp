package com.jqwong.music.helper

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.jqwong.music.app.App
import com.jqwong.music.event.*
import com.jqwong.music.model.*
import com.jqwong.music.service.KuWOService
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.lang.Runnable

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class AudioHelper {
    @UnstableApi companion object{
        private val TAG = "AudioHelper"
        private val MAX_RELOAD_COUNT = 1
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
            _session = MediaSession.Builder(ctx, _player)
                .setCallback(object:MediaSession.Callback{
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onPlayerCommandRequest(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        playerCommand: Int
                    ): Int {
                        // play&pause=1, next=9,prev=7
                        when(playerCommand){
                            9 -> {
                                prepare()
                            }
                        }
                        return super.onPlayerCommandRequest(session, controller, playerCommand)
                    }
                })
                .build()
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

                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    // 加载歌词 & 准备下一首歌曲
                    if(!(mediaMetadata.extras?.getString(ExtraKey.Audio.name).isNullOrEmpty())){
                        val audio = mediaMetadata.extras?.getString(ExtraKey.Audio.name)!!.toAudio()
                        val index = App.playList.data.getIndex(audio)
                        if(index != -1){
                            App.playList.index = index
                            EventBus.getDefault().post(MediaChangeEvent(App.playList.data.get(index)))
                            EventBus.getDefault().post(MediaLoadingEvent(finish = false))
                            EventBus.getDefault().post(LyricsLoadingEvent(false))

                            var platform = audio.platform
                            var id = audio.id
                            if(audio.changeInfo != null){
                                platform = audio.changeInfo!!.platform!!
                                id = audio.changeInfo!!.id!!
                            }
                            getLyrics(platform,id,0) { success, lyrics ->
                                if(success){
                                    App.playList.lyrics = lyrics
                                }
                                else{
                                    App.playList.lyrics = null
                                }
                                EventBus.getDefault().post(MediaLoadingEvent(finish = true))
                                EventBus.getDefault().post(LyricsLoadingEvent(true))
                            }
                            prepare()
                        }
                    }
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    when(playbackState){
                        Player.STATE_READY -> {
                            var playing = false
                            if(_player.isPlaying)
                                playing = true
                            EventBus.getDefault().post(PlayerStatusChangeEvent(playing))
                        }
                        Player.STATE_ENDED -> {
                            EventBus.getDefault().post(PlayerStatusChangeEvent(false))
                        }
                    }
                }
            })
        }
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun start(){
            if(!App.playListIsInitialized())
                return
            _player.stop()
            _player.clearMediaItems()
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
                        }
                        else {
                            change(media.audio!!){
                                if(it == null){
                                    App.playList.index++
                                    start()
                                }
                                else{
                                    media.audio!!.changeInfo = it
                                    App.playList.data.get(App.playList.index).audio!!.play_url = it.url!!
                                    _player.addMediaItem(media.audio!!.build())
                                    _player.prepare()
                                    EventBus.getDefault().post(MediaLoadingEvent(finish = true))
                                }
                            }
                        }
                    }
                }
            }
        }
        fun prev(){
            if(this::_player.isInitialized){
                _player.seekToPrevious()
            }
        }
        fun next(){
            if(this::_player.isInitialized){
                _player.seekToNext()
            }
        }
        fun playOrPause(){
            if(this::_player.isInitialized){
                if(_player.isPlaying){
                    _player.pause()
                }
                else{
                    _player.play()
                }
            }
        }
        fun getPosition():Long{
            return _player.currentPosition
        }
        fun getPlayerIsPlaying():Boolean{
            if(!this::_player.isInitialized)
                return false
            return _player.isPlaying
        }
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun prepare(loadIndex:Int = 0){
            if(_player.hasNextMediaItem())
                return
            if(App.playList.index == App.playList.data.count() - 1)
                return
            var index = 0
            if(loadIndex != 0){
                index = loadIndex
            }
            else{
                index = App.playList.index+1
            }
            val media = App.playList.data.get(index)
            if(media.audio != null){
                if(media.audio!!.play_url.isNullOrEmpty()){
                    getPlayUrl(media.audio!!.platform,media.audio!!.id, getPlayQuality(media.audio!!.platform),0) { success: Boolean, url: String ->
                        if (success) {
                            media.audio!!.play_url = url
                            _player.addMediaItem(media.audio!!.build())
                        }
                        else {
                            change(media.audio!!){
                                if(it == null){
                                    App.playList.index++
                                    start()
                                }
                                else{
                                    media.audio!!.changeInfo = it
                                    media.audio!!.play_url = it.url
                                    _player.addMediaItem(media.audio!!.build())
                                }
                            }
                        }
                    }
                }
            }
            else{
                // TODO
            }
        }
        private fun getPlayQuality(platform: Platform):Any{
            when(platform){
                Platform.KuWo-> return "flac"
                Platform.NetEaseCloud->return App.config.netEaseCloudMusicConfig.quality
                Platform.KuGou-> return ""
                else -> return ""
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        private fun getLyrics(
            platform: Platform,
            id:String,
            reloadNumber:Int=0,
            call: (success:Boolean,lyrics:Lyrics?) -> Unit
        ){
            CoroutineScope(Dispatchers.IO).launch {
                if(reloadNumber != 0){
                    delay(1500)
                }
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
        @RequiresApi(Build.VERSION_CODES.O)
        private fun getPlayUrl(
            platform: Platform,
            id:String,
            quality:Any,
            reloadNumber:Int=0,
            call: (success:Boolean,url:String) -> Unit
        ){
            CoroutineScope(Dispatchers.IO).launch {
                if(reloadNumber != 0){
                    delay(1500)
                }
                val result = ServiceProxy.getPlayUrl(platform,id,quality)
                withContext(Dispatchers.Main){
                    if(result.exception != null){
                        if(reloadNumber == MAX_RELOAD_COUNT){
                            //Toast.makeText(_ctx,result.exception.exception.message.toString(),Toast.LENGTH_SHORT).show()
                            App.exceptions.add(result.exception)
                            call.invoke(false,"")
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

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun change(audio: Audio, call: (info:AudioChangeInfo?) -> Unit){
            val name = audio.name
            val artist = audio.artists.first()
            val platform = Platform.KuWo
            CoroutineScope(Dispatchers.IO).launch {
                val service = ServiceProxy.getService(platform).data as KuWOService
                val result = service.search(name,1,10)
                if(result.exception != null){
                    withContext(Dispatchers.Main){
                        call(null)
                    }
                }
                else{
                    var exist = false
                    result.data!!.forEach {
                        if(!exist){
                            if(it.audio != null && it.audio!!.name == name && it.audio!!.artists.toName().contains(artist.name)){
                                val audio2 = it.audio
                                val result2 = service.getPlayUrl(audio2!!.id, getPlayQuality(platform))
                                if(result2.exception == null){
                                    exist = true
                                    withContext(Dispatchers.Main){
                                        call(AudioChangeInfo(platform = platform, id = audio2.id, url = result2.data!!, data = null))
                                    }
                                }
                            }
                        }
                    }
                    if(!exist){
                        withContext(Dispatchers.Main){
                            Toast.makeText(_ctx,"not found about '${audio.name} media'",Toast.LENGTH_SHORT).show()
                            call(null)
                        }
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