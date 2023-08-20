package com.jqwong.music.helper

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.jqwong.music.app.App
import com.jqwong.music.event.*
import com.jqwong.music.model.*
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.io.File
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

                    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                    if(!(mediaMetadata.extras?.getString(ExtraKey.Media.name).isNullOrEmpty())){
                        val json = mediaMetadata.extras?.getString(ExtraKey.Media.name)!!
                        val media = Media.fromJson(json)
                        val index = App.playList.data.getIndex(media)
                        if(index != -1){
                            App.playList.index = index
                            EventBus.getDefault().post(MediaChangeEvent(App.playList.data.get(index)))
                            EventBus.getDefault().post(MediaLoadingEvent(finish = false))
                            EventBus.getDefault().post(LyricsLoadingEvent(false))

                            var platform = media.platform
                            var id = media.id
                            if(media.enable_media != null){
                                platform = media.enable_media!!.platform
                                id = media.enable_media!!.id
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
            val current = App.playList.data.get(App.playList.index)
            EventBus.getDefault().post(MediaChangeEvent(current))
            EventBus.getDefault().post(MediaLoadingEvent(false))
            // 做缓存处理，避免重复请求
            getMedia(App.playList.index){success, media ->
                if(!success || media == null){
                    App.playList.index++
                    start()
                }
                else{
                    var playMedia = current
                    if(current.platform == media.platform){
                        playMedia = media
                    }
                    else{
                        current.enable_media = media
                        playMedia = current
                    }
                    _player.addMediaItem(playMedia.build())
                    _player.prepare()
                    EventBus.getDefault().post(MediaLoadingEvent(finish = true))
                }
                EventBus.getDefault().post(MediaLoadingEvent(true))
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
            val next = App.playList.data.get(index)
            getMedia(index){success, media ->
                if(!success || media == null){
                    prepare(index+1)
                }
                else{
                    if(next.platform == media.platform){
                        media.let {
                            next.play_url = it.play_url
                            next.play_uri = it.play_uri
                            next.is_local = it.is_local
                        }
                        _player.addMediaItem(next.build())
                    }
                    else{
                        next.enable_media = media
                        _player.addMediaItem(next.build())
                    }
                }
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
        private fun getMedia(index:Int, call:(success:Boolean, media:Media?) -> Unit){
            CoroutineScope(Dispatchers.IO).launch{
                if(index < 0 || index >= App.playList.data.count()){
                    withContext(Dispatchers.Main){
                        call(false,null)
                    }
                    return@launch
                }
                val media = App.playList.data.get(index)
                val pfMap = mutableListOf(
                    Platform.KuWo,
                    Platform.NetEaseCloud,
                )
                for(i in pfMap.count()-1 downTo 0){
                    if(pfMap.get(i) == media.platform){
                        pfMap.removeAt(i)
                        break
                    }
                }
                pfMap.add(0,media.platform)
                val medias = mutableMapOf<Platform,Media?>(
                    media.platform to media
                )

                // 获取url
                for (i in 0 until pfMap.count()){
                    val pf = pfMap[i]
                    if(!medias.containsKey(pf)){
                        medias.put(pf, getSimilarMedia(pf,media.name,media.artists.first().name))
                    }
                    val item = medias.get(pf)
                    if(item != null){
                        val url = getPlayUrl(item, getPlayQuality(pf),1)
                        if(!url.isNullOrEmpty()){
                            item.play_url = url
                            withContext(Dispatchers.Main){
                                call(true,item)
                            }
                            return@launch
                        }
                    }
                }

                // 获取uri
                run _break@{
                    medias.entries.forEach {
                        if(it.value != null){
                            val item = it.value!!
                            item.is_local = true
                            val file = "${item.platform.name}-${item.id}.aac"
                            val dir = "${_ctx.cacheDir.path}/media/"
                            val path = "${dir}${file}"
                            val fDir = File(dir)
                            if(!fDir.exists()){
                                fDir.mkdir()
                            }
                            else{
                                val fList = fDir.listFiles()
                                for (i in 0 until fList.size){
                                    if(fList.get(i).path == path){
                                        item.play_uri = path
                                        withContext(Dispatchers.Main){
                                            call(true,item)
                                        }
                                        return@launch
                                    }
                                }
                            }
                            val result = ServiceProxy.getMvUrl(item.platform,item.mv_id!!)
                            if(result.exception == null && !result.data.isNullOrEmpty()){
                                item.mv_url = result.data
                                //val cmd = "-i ${result.data} -f mp3 -vn ${path}"
                                val cmd = "-i ${result.data} -vn -acodec copy ${path}"
                                val rc = FFmpeg.execute(cmd)
                                if (rc == Config.RETURN_CODE_SUCCESS) {
                                    item.play_uri = path
                                    withContext(Dispatchers.Main){
                                        call(true,item)
                                    }
                                    return@launch
                                }else if (rc == Config.RETURN_CODE_CANCEL) {
                                    Log.i(Config.TAG, "Command execution cancelled by user.");
                                } else {
                                    Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                                    Config.printLastCommandOutput(Log.INFO);
                                }
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main){
                    call(false,null)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun getPlayUrl(media: Media, quality:Any, maxReload:Int, reload:Int = 0):String?{
            if(reload != 0){
                delay((reload * 1000).toLong())
            }
            val result = ServiceProxy.getPlayUrl(media.platform,media.id,quality)
            if(result.exception != null){
                if(reload == maxReload){
                    return null
                }
                else{
                    return getPlayUrl(media,quality,maxReload,reload+1)
                }
            }
            return result.data
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun getSimilarMedia(platform: Platform, name:String, artist:String,):Media?{
            val key = "${name} ${artist}"
            val result = ServiceProxy.search(platform,key,1,10)
            if(result.exception != null)
                return null
            result.data!!.forEach {
                if(it.name == name && it.artists.toName().contains(artist))
                    return it
            }
            return null
        }
    }

    class PositionListener : Runnable {
        override fun run() {
            EventBus.getDefault().post(MediaPositionChangeEvent(_player.currentPosition))
            _h.postDelayed(this, 100)
        }

    }
}