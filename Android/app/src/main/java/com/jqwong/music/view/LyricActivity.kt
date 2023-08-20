package com.jqwong.music.view

import android.os.Bundle
import android.view.Menu
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.jqwong.music.helper.startAnimation
import com.jqwong.music.model.current
import com.jqwong.music.view.layoutManager.CenterLayoutManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@UnstableApi
/**
 * @author: Jq
 * @date: 7/24/2023
 */
class LyricActivity:BaseActivity<ActivityLyricBinding>() {
    private lateinit var adapter:LyricAdapter
    override fun initData(savedInstanceState: Bundle?) {
    }

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
            onLyricsLoadingEvent(LyricsLoadingEvent(App.playList.lyrics != null))
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricsLoadingEvent(event: LyricsLoadingEvent){
        if(!event.finish){
            _binding.stateLayout.showLoading()
        }
        else{
            if(App.playList.lyrics == null){
                _binding.stateLayout.empty("Sorry")
            }
            else{
                val list = App.playList.lyrics
                adapter.submitList(list!!.lyrics)
                val current = list.current(AudioHelper.getPosition())
                _binding.rvList.layoutManager!!.scrollToPosition(adapter.getItemPosition(current))
                _binding.stateLayout.content()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lyric,menu)
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPositionChangeEvent(event:MediaPositionChangeEvent){
        val current = App.playList.lyrics!!.current(event.position)
        if(adapter.currentIsInitialized() && adapter.current.time == current.time)
            return
        adapter.current = current
        _binding.rvList.layoutManager?.let {
            it.smoothScrollToPosition(_binding.rvList, RecyclerView.State(),adapter.getItemPosition(adapter.current))
        }
        adapter.notifyDataSetChanged()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStatusChangeEvent(event: PlayerStatusChangeEvent){
        _binding.btnPlay.setImageResource(if(event.playing) R.drawable.ic_pause else R.drawable.ic_play)
    }
}