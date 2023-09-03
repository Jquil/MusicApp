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
            _player.playWhenReady = true
            val current = App.playList.data.get(App.playList.index)
            EventBus.getDefault().post(MediaChangeEvent(current))
            EventBus.getDefault().post(MediaLoadingEvent(false))
            getMedia(App.playList.index){success, media ->
                if(!success || media == null){
                    App.playList.index++
                    start()
                }
                else{
                    var playMedia = current
                    if(current.platform == media.platform && current.id == media.id){
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
                Platform.NetEaseCloud->return App.config.netEaseCloudConfig.quality
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
                    //(1500)
                }
                val result = ServiceProxy.get(platform).data?.getLyrics(id)!!
                withContext(Dispatchers.Main){
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
            // 跟随配置动态请求
            CoroutineScope(Dispatchers.IO).launch{
                if(index < 0 || index >= App.playList.data.count()){
                    withContext(Dispatchers.Main){
                        call(false,null)
                    }
                    return@launch
                }
                var media = App.playList.data.get(index)

                // 调整切换平台顺序,将当前音乐平台放在首位置
                val list = mutableListOf<ChangePlatformItem>()
                list.addAll(App.config.change_platform_priority)
                for (i in 0 until list.size){
                    if (list[i].platform == media.platform){
                        val item = list[i]
                        list.removeAt(i)
                        list.add(0,item)
                        break
                    }
                }

                // 生成请求队列
                val tskList = mutableListOf<String>()
                val tskList2 = mutableListOf<String>()
                for (i in 0 until list.size){
                    val item = list[i]
                    if(!item.enable)
                        continue
                    val key = "${item.platform.name}-${item.mode.name}"
                    when(item.mode){
                        ChangePlatformMode.OnlyFromPlayUrl -> {
                            tskList.add(key)
                        }
                        ChangePlatformMode.OnlyFromParseMv -> {
                            tskList.add(key)
                        }
                        ChangePlatformMode.AllOfTheAbove -> {
                            // 优先请求播放地址, 但播放地址全部失效再来解析mv
                            tskList.add("${item.platform.name}-${ChangePlatformMode.OnlyFromPlayUrl.name}")
                            tskList2.add("${item.platform.name}-${ChangePlatformMode.OnlyFromParseMv.name}")
                        }
                    }
                }
                tskList.addAll(tskList2)

                // 开始请求
                val mediaList = mutableMapOf<Platform,List<Media>>()
                tskList.forEach {
                    val arr = it.split('-')
                    val platform = Platform.valueOf(arr[0])
                    val mode = ChangePlatformMode.valueOf(arr[1])
                    if(!mediaList.containsKey(platform)){
                        if(media.platform == platform){
                            mediaList.put(platform, listOf(media))
                        }
                        else{
                            mediaList.put(platform, getSimilarMediaList(platform,media))
                        }
                    }
                    mediaList.get(platform)!!.forEach {
                        when(mode){
                            ChangePlatformMode.OnlyFromPlayUrl -> {
                                var url = getPlayUrl(it, getPlayQuality(platform),1)
                                if(!url.isNullOrEmpty()){
                                    if(it.platform == Platform.NetEaseCloud){
                                        url = "${url}?id=id.mp3"
                                    }
                                    it.play_url = url
                                    withContext(Dispatchers.Main){
                                        call(true,it)
                                    }
                                    return@launch
                                }
                            }
                            ChangePlatformMode.OnlyFromParseMv -> {
                                if(App.config.allow_use_ffmpeg_parse){
                                    if(App.config.only_wifi_use_ffmpeg_parse && !WifiHelper.isConnected(_ctx)){
                                        // 只允许wifi连接但手机没连接wifi, 不允许使用ffmpeg解析
                                    }
                                    else{
                                        it.is_local = true
                                        val file = it.filename()
                                        val dir = "${_ctx.cacheDir.path}/media/"
                                        val path = "${dir}${file}"
                                        val fDir = File(dir)
                                        if(!fDir.exists()){
                                            fDir.mkdir()
                                        }
                                        else{
                                            val fList = fDir.listFiles()
                                            if (fList != null) {
                                                for (i in 0 until fList.size){
                                                    if(fList.get(i).path == path){
                                                        it.play_url = path
                                                        withContext(Dispatchers.Main){
                                                            call(true,it)
                                                        }
                                                        return@launch
                                                    }
                                                }
                                            }
                                        }
                                        val result = ServiceProxy.get(it.platform).data?.getMvUrl(it.mv_id!!)!!
                                        if(result.exception == null && !result.data.isNullOrEmpty()){
                                            it.mv_url = result.data
                                            val parseResult = FFmPegHelper.getAudio(result.data,path)
                                            if(parseResult.first){
                                                it.play_url = path
                                                withContext(Dispatchers.Main){
                                                    call(true,it)
                                                }
                                                return@launch
                                            }
                                        }
                                    }
                                }
                            }
                            else->{}
                        }
                    }
                }

                // 全部请求失败
                withContext(Dispatchers.Main){
                    call(false,null)
                }
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun getPlayUrl(media: Media, quality:Any, maxReload:Int, reload:Int = 0):String?{
            if(reload != 0){
                //delay((reload * 1000).toLong())
            }
            val result = ServiceProxy.get(media.platform).data?.getPlayUrl(media.id,quality)!!
            if(result.exception != null){
                if(reload == maxReload){
                    return null
                }
                else{
                    return getPlayUrl(media,quality,maxReload,reload+1)
                }
            }
            if(!result.success)
                return null
            return result.data
        }
        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun getSimilarMediaList(platform: Platform, media:Media,maxSize:Int = 3):List<Media>{
            var key = "${media.name} ${media.artists.first().name}"
            var name2 = media.name
            if(media.name.contains('(')){
                val arr = media.name.split('(')
                name2 = arr.first().trim()
                key = "$name2 ${media.artists.first().name}"
            }
            val service = ServiceProxy.get(platform).data ?: return listOf()
            val result = service.search(key,1,10)
            if(result.exception != null)
                return listOf()
            val list = mutableListOf<Media>()
            val spareList = mutableListOf<Media>()
            result.data!!.forEach {
                if(it.name.contains(name2,ignoreCase = true)){
                    if(media.artists.compare(it.artists)){
                        list.add(it)
                    }
                    if(media.artists.exist(it.artists)){
                        spareList.add(it)
                    }
                }
            }
            list.addAll(spareList)
            // 限制三条记录
            if(list.count() > maxSize){
                for(i in list.count()-1 downTo 0){
                    list.removeAt(i)
                    if(i == maxSize){
                        break
                    }
                }
            }
            return list
        }
    }
    class PositionListener : Runnable {
        override fun run() {
            EventBus.getDefault().post(MediaPositionChangeEvent(_player.currentPosition))
            _h.postDelayed(this, 100)
        }
    }
}