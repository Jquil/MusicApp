package com.jqwong.music.view

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jqwong.music.R
import com.jqwong.music.adapter.LyricAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityLyricBinding
import com.jqwong.music.event.LyricsLoadingEvent
import com.jqwong.music.event.MediaChangeEvent
import com.jqwong.music.event.MediaPositionChangeEvent
import com.jqwong.music.helper.startAnimation
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

    override fun intView() {
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
        _binding.layoutHeader.tvName.text = if(media.video == null) media.audio!!.name else media.video!!.title
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricsLoadingEvent(event: LyricsLoadingEvent){
        if(!event.finish){
            _binding.stateLayout.showLoading()
        }
        else{
            if(App.playList.lyrics == null){
                // show empty
            }
            else{
                val list = App.playList.lyrics
                adapter.submitList(list!!.lyrics)
                _binding.stateLayout.apply {
                    onContent {
                        this@apply.startAnimation()
                    }
                    this.showContent()
                }
            }
        }
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
}