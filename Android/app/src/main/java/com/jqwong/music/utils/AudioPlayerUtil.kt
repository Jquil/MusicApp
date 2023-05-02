package com.jqwong.music.utils

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.google.gson.Gson
import com.jqwong.music.app.Constant
import com.jqwong.music.app.Global
import com.jqwong.music.event.*
import com.jqwong.music.model.Call
import com.jqwong.music.model.Lyric
import com.jqwong.music.model.Media
import com.jqwong.music.model.MediaStatus
import com.jqwong.music.service.KuWoService
import org.greenrobot.eventbus.EventBus

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class AudioPlayerUtil {

    companion object{
        private lateinit var _ctx:Context
        private lateinit var _session:MediaSession
        private lateinit var _player:Player
        private lateinit var _listenPositionTask:ListenPositionTask
        private val _urlMap = HashMap<Int,String>()
        private val _queue = mutableListOf<Media>()
        private val _h = Handler()
        private val _kuwoService = KuWoService()
        fun init(ctx:Context){
            _ctx = ctx
            _player = ExoPlayer.Builder(ctx)
                .setLooper(Looper.getMainLooper())
                .build()
            _session = MediaSession.Builder(ctx, _player).build()
            _player.playWhenReady = true
            _player.addListener(object:Listener{

                // 监听播放进度
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)){
                        if (_player.isPlaying){
                            _listenPositionTask = ListenPositionTask()
                            _h.postDelayed(_listenPositionTask,100)
                        }
                        else{
                            _h.removeCallbacks(_listenPositionTask)
                        }
                    }
                }

                // 加载歌词&缓存下一首歌曲
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    val objStr = mediaMetadata.extras?.getString(Constant.ExtraKey)
                    if(objStr == null || objStr == "")
                        return
                    val media = Gson().fromJson(objStr,Media::class.java)
                    media.time = DateTimeUtil.minutesToTime(media.songTimeMinutes)
                    Global.media = media
                    EventBus.getDefault().post(MediaChangeEvent(media))
                    EventBus.getDefault().post(LyricLoadEvent(LyricLoadStatus.LOADING,null))
                    _kuwoService.getLyric(media.rid.toString(),object:Call<Lyric>{
                        override fun success(t: Lyric) {
                            t.data.lrclist.forEach {
                                it.inTime = (it.time.toFloat() * 1000).toLong()
                            }
                            Global.lyric = t
                            EventBus.getDefault().post(LyricLoadEvent(LyricLoadStatus.SUCCESS,t))
                            EventBus.getDefault().post(LyricIndexChangeEvent(0))
                            if (!_player.hasNextMediaItem()){
                                prepareNextItem()
                            }
                        }

                        override fun error(e: Throwable) {
                            Global.lyric = null
                            EventBus.getDefault().post(LyricLoadEvent(LyricLoadStatus.ERROR,null))
                            EventBus.getDefault().post(LyricIndexChangeEvent(0))
                            if (!_player.hasNextMediaItem()){
                                prepareNextItem()
                            }
                            toast(e.message.toString())
                        }

                    })
                }

                // 更新播放器状态
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    when(playbackState){
                        Player.STATE_READY -> {
                            var status = MediaStatus.PAUSE
                            if(_player.isPlaying)
                                status = MediaStatus.PLAYING
                            EventBus.getDefault().post(PlayerStatusChangeEvent(status))
                        }
                        Player.STATE_ENDED -> {
                            EventBus.getDefault().post(PlayerStatusChangeEvent(MediaStatus.PAUSE))
                        }
                    }
                }

            })
        }


        /**
         * 下一首
         */
        fun next(){
            _player.seekToNext()
        }

        /**
         * 上一首
         */
        fun previous(){
            _player.seekToPrevious()
        }

        /**
         * 加入下一首歌曲进待播放队列
         */
        fun prepareNextItem(){
            if(_queue.size == 0){
                toast("play finish")
                return
            }
            var media = _queue.first()
            _queue.removeAt(0)
            val callAddItem = object:Call<Media>{
                override fun success(m: Media) {
                    val bundle = Bundle()
                    bundle.putString(Constant.ExtraKey, Gson().toJson(m))
                    val mediaItem = MediaItem.Builder()
                        .setUri(m.url)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setExtras(bundle)
                                .setAlbumTitle(m.album)
                                .setAlbumArtist(m.artist)
                                .setArtist(m.artist)
                                .setTitle(m.name)
                                .setArtworkUri(Uri.parse("https://jqwong.cn/file/music_app_artwork.png"))
                                .build())
                        .build()
                    _player.addMediaItem(mediaItem)
                    _player.prepare()
                }

                override fun error(e: Throwable) {
                    toast(e.message.toString())
                }

            }
            if(_urlMap.containsKey(media.rid)){
                media.url = _urlMap.get(media.rid)
                callAddItem.success(media)
            }
            else{
                _kuwoService.getPlayUrl(media.rid.toString(),object: Call<String> {
                    override fun success(data: String) {
                        _urlMap.put(media.rid,data)
                        media.url = data
                        callAddItem.success(media)
                    }

                    override fun error(e: Throwable) {
                        toast(e.message.toString())
                        prepareNextItem()
                    }
                })
            }
        }

        /**
         * 播放
         * @param t List<Media>
         */
        fun play(t:List<Media>){
            _player.stop()
            _player.clearMediaItems()
            _queue.clear()
            _queue.addAll(t)
            prepareNextItem()
        }

        /**
         * 改变播放器状态
         */
        fun changeStatus(){
            if(_player.isPlaying){
                _player.pause()
            }
            else{
                if(_player.currentMediaItem != null){
                    _player.play()
                }
            }
        }


        /**
         * 获取播放器状态
         * @return MediaStatus
         */
        fun getPlayerStatus():MediaStatus{
            var status = MediaStatus.PAUSE
            if(_player.isPlaying){
                status = MediaStatus.PLAYING
            }
            return status
        }

        /**
         * 跳转
         * @param position Long
         */
        fun seekToPosition(position:Long){
            if (_player.mediaItemCount == 0)
                return
            if(Global.media == null)
                return
            _player.seekTo(position)
        }

        /**
         * 是否重复播放
         * @param mode Int
         */
        fun setRepeatMode(mode:Int){
            _player.repeatMode = mode
        }


        /**
         * 获取播放器是否重复播放
         * @return Int
         */
        fun getRepeatMode():Int{
            return _player.repeatMode
        }


        fun toast(message:String){
            Toast.makeText(_ctx,message,Toast.LENGTH_SHORT).show()
        }
    }

    class ListenPositionTask : Runnable {
        override fun run() {
            EventBus.getDefault().post(MediaPositionChangeEvent(_player.currentPosition))
            _h.postDelayed(this, 100)
        }
    }
}