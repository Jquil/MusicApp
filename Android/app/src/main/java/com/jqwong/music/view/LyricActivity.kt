package com.jqwong.music.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.jqwong.music.R
import com.jqwong.music.adapter.LyricAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityLyricBinding
import com.jqwong.music.event.LyricsLoadingEvent
import com.jqwong.music.event.MediaChangeEvent
import com.jqwong.music.event.MediaPositionChangeEvent
import com.jqwong.music.event.PlayerStatusChangeEvent
import com.jqwong.music.helper.AudioHelper
import com.jqwong.music.helper.content
import com.jqwong.music.helper.empty
import com.jqwong.music.model.LyricStatus
import com.jqwong.music.model.current
import com.jqwong.music.view.layoutManager.CenterLayoutManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author: Jq
 * @date: 7/24/2023
 */

class LyricActivity:BaseActivity<ActivityLyricBinding>() {
    private lateinit var adapter:LyricAdapter
    override fun initData(savedInstanceState: Bundle?) {
    }

    @UnstableApi
    override fun intView() {
        _binding.includeToolbar.toolbar.elevation = 0f
        setSupportActionBar(_binding.includeToolbar.toolbar)
        supportActionBar?.title = ""
        adapter = LyricAdapter()
        val manager = CenterLayoutManager(this)
        manager.orientation = (RecyclerView.VERTICAL)
        _binding.rvList.isVerticalFadingEdgeEnabled = true
        _binding.rvList.layoutManager = manager
        _binding.rvList.adapter = adapter
        if(App.playListIsInitialized()){
            _binding.stateLayout.showLoading()
            val media = App.playList.current()
            onMediaChangeEvent(MediaChangeEvent(media))
            onLyricsLoadingEvent(LyricsLoadingEvent(App.playList.lyricInfo))
        }
        _binding.includeToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.btnPlay.setOnClickListener {
            AudioHelper.playOrPause()
        }
        _binding.btnPrev.setOnClickListener {
            AudioHelper.prev()
        }
        _binding.btnNext.setOnClickListener {
            AudioHelper.next()
        }
        onPlayerStatusChangeEvent(PlayerStatusChangeEvent(AudioHelper.getPlayerIsPlaying()))
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun statusBarColor(): Int {
        return R.color.background
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event:MediaChangeEvent){
        val media = App.playList.data.get(App.playList.index)
        _binding.tvName.text = media.name
    }

    @UnstableApi
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricsLoadingEvent(event: LyricsLoadingEvent){
        when(event.info.first){
            LyricStatus.Loading->{
                _binding.stateLayout.showLoading()
            }
            LyricStatus.Error->{
                _binding.stateLayout.empty("骚瑞, 没有找到歌曲歌词噢...")
            }
            LyricStatus.Success->{
                if(event.info.second == null){
                    _binding.stateLayout.empty("骚瑞, 没有找到歌曲歌词噢...")
                }
                else{
                    val list = App.playList.lyricInfo.second
                    adapter.submitList(list!!.lyrics)
                    val current = list.current(AudioHelper.getPosition())
                    _binding.rvList.layoutManager!!.scrollToPosition(adapter.getItemPosition(current))
                    _binding.stateLayout.content()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lyric,menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.action_artist -> {
                if(App.playListIsInitialized() && App.playList.data.isNotEmpty()){
                    val media = App.playList.current()
                    gotoArtistActivity(media)
                }
            }
            R.id.action_collect -> {
                if(App.playListIsInitialized() && App.playList.data.isNotEmpty()){
                    val media = App.playList.current()
                    collectOrCancelMedia(media.platform,null,media,true){}
                }
            }
            R.id.action_change_platform -> {
                if(App.playListIsInitialized() && App.playList.data.isNotEmpty()){
                    val media = App.playList.current()
                    changePlatform(media.platform,media.name)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPositionChangeEvent(event:MediaPositionChangeEvent){
        val current = App.playList.lyricInfo.second!!.current(event.position)
        if(!adapter.currentIsInitialized()){
            adapter.current = current
            adapter.notifyDataSetChanged()
        }
        if(adapter.current.time == current.time)
            return
        val prevPosition = adapter.getItemPosition(adapter.current)
        adapter.current = current
        val position = adapter.getItemPosition(current)
        if(Math.abs(position - prevPosition) > 5){
            _binding.rvList.layoutManager?.scrollToPosition(position)
        }
        else{
            _binding.rvList.layoutManager?.smoothScrollToPosition(_binding.rvList, RecyclerView.State(),position)
        }
        adapter.notifyDataSetChanged()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStatusChangeEvent(event: PlayerStatusChangeEvent){
        _binding.btnPlay.setImageResource(if(event.playing) R.drawable.ic_pause else R.drawable.ic_play)
    }
}