package com.jqwong.music.utils.audio

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.*
import androidx.media3.common.Player.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.BitmapLoader
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.Callback
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.model.*
import com.jqwong.music.service.SearchService
import com.jqwong.music.utils.eventbus.event.*
import org.greenrobot.eventbus.EventBus

class AudioPlayerUtil {


    companion object{

        private lateinit var _Session:MediaSession

        private lateinit var _Player:Player

        private var _PlayList = mutableListOf<Media>()

        private val _H = Handler()

        private var TAG = "AudioPlayerUtil"

        private var KEY = "media"

        private lateinit var _LT:ListenPositionTask

        fun Init(ctx:Context){
            _Player = ExoPlayer.Builder(ctx)
                .setLooper(Looper.getMainLooper())
                .build()
            _Player.playWhenReady = true
            _Player.addListener(object:Listener{
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    when(playbackState){
                        Player.STATE_IDLE -> {
                            //Log.e(TAG,"onPlayerStateChanged: NoExit media")
                        }
                        Player.STATE_BUFFERING -> {
                            //Log.e(TAG,"onPlayerStateChanged: Loading")
                        }
                        Player.STATE_READY -> {
                            var state = MediaState.Pause
                            if(_Player.isPlaying)
                                state = MediaState.Play
                            EventBus.getDefault().post(MediaStateChangeEvent(state))
                        }
                        Player.STATE_ENDED -> {
                            if(_Player.hasNextMediaItem()){
                                Log.e(TAG,"contain next media item")
                            }
                            else{
                                Log.e(TAG,"no contain")
                            }
                        }
                    }
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)){
                        if (_Player.isPlaying){
                            _LT = ListenPositionTask()
                            _H.postDelayed(_LT,100)
                        }
                        else{
                            _H.removeCallbacks(_LT)
                        }
                    }
                }


                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    val objStr = mediaMetadata.extras?.getString(KEY)
                    if(objStr == null || objStr == "")
                        return

                    try {
                        var media = Gson().fromJson(objStr,Media::class.java)
                        //Log.e(TAG,"onMediaMetadataChanged: request ${media.name} lyric")
                        GlobalObject.CurrentMedia = media
                        EventBus.getDefault().post(MediaChangeEvent(media))
                        EventBus.getDefault().post(LyricLoadingEvent())
                        var sService = SearchService()
                        sService.GetLyric(media.rid.toInt(),object: HttpCall<Lyric> {
                            override fun onError(e: Throwable) {
                                //Toast.makeText(this@ResultActivity,e.message.toString(), Toast.LENGTH_SHORT).show()
                                //Log.e(TAG,"error: onMediaMetadataChanged:${e.message.toString()}")
                                GlobalObject.Lyric = null
                                EventBus.getDefault().post(LyricChangeEvent())
                                EventBus.getDefault().post(LyricIndexChangeEvent(0))
                            }

                            override fun OnSuccess(t: Lyric) {

                                if(t.data != null && t.data.lrclist != null){
                                    t.data.lrclist.forEach {
                                        it.inTime = (it.time.toFloat() * 1000).toLong()
                                    }
                                }
                                //Log.e(TAG,"Get lyric success");
                                GlobalObject.Lyric = t
                                EventBus.getDefault().post(LyricChangeEvent(t))
                                EventBus.getDefault().post(LyricIndexChangeEvent(0))

                                // prepare next media item
                                if(!_Player.hasNextMediaItem()){
                                    AddMediaItem()
                                }
                            }
                        })
                    }
                    catch (e:java.lang.Exception){
                        //Log.e(TAG,"error: onMediaMetadataChanged:${e.message.toString()}")
                    }
                }

                override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                    super.onDeviceVolumeChanged(volume, muted)
                    //Log.e(TAG,"onDeviceVolumeChanged:${volume}")
                }


            })
            _Session = MediaSession.Builder(ctx, _Player)
                .setCallback(object:Callback{

                })
                .build()
        }


        fun PrepareAndPlay(list: List<Media>){
            _Player.stop()
            _Player.clearMediaItems()
            _PlayList.clear()
            _PlayList.addAll(list)
            AddMediaItem(prepare = true)
        }


        fun Next(){
            _Player.seekToNext()
        }


        fun Pre(){
            _Player.seekToPrevious()
        }


        fun PlayOrPause(){
            if (_Player.isPlaying)
                _Player.pause()
            else{
                if(_Player.currentMediaItem != null){
                    _Player.play()
                }
            }
        }


        private fun AddMediaItem(prepare:Boolean=false){
            if(_PlayList.size == 0)
                return

            var media = _PlayList.first()
            _PlayList.removeAt(0)
            SearchService().GetPlayUrl(media.rid.toInt(),object:HttpCall<String>{
                override fun onError(e: Throwable) {
                    AddMediaItem(prepare)
                }

                override fun OnSuccess(t: String) {
                    media.url = t
                    val bundle = Bundle()
                    bundle.putString(KEY,Gson().toJson(media))
                    val mediaItem = MediaItem.Builder()
                        .setUri(media.url)
                        .setMediaMetadata(MediaMetadata.Builder()
                            .setExtras(bundle)
                            .setAlbumTitle(media.album)
                            .setAlbumArtist(media.artist)
                            .setArtist(media.artist)
                            .setTitle(media.name)
                            .setArtworkUri(Uri.parse("https://jqwong.cn/static/img/avatar1.aefad32.png"))
                            .build())
                        .build()
                    _Player.addMediaItem(mediaItem)
                    if(prepare)
                        _Player.prepare()
                }
            })
        }


        fun GetState():MediaState{
            var state = MediaState.Pause
            if(_Player.isPlaying)
                state = MediaState.Play
            return state
        }


        fun SeekToPosition(position:Long){
            if (_Player.mediaItemCount == 0)
                return
            if(GlobalObject.CurrentMedia == null)
                return
            _Player.seekTo(position)
        }


        fun IsRepeatMode():Int{
            return _Player.repeatMode
        }


        fun SetRepeatMode(mode:Int){
            _Player.repeatMode = mode
        }
    }



     class ListenPositionTask : Runnable {
        override fun run() {
            EventBus.getDefault().post(MediaPositionChangeEvent(_Player.currentPosition))
            //Log.e(TAG,"${_Player.currentPosition}")
            _H.postDelayed(this, 100)
        }
    }

}