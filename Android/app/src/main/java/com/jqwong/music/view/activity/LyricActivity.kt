package com.jqwong.music.view.activity

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.jqwong.music.R
import com.jqwong.music.adapter.LyricAdapter
import com.jqwong.music.app.Global
import com.jqwong.music.databinding.ActivityLyricBinding
import com.jqwong.music.event.*
import com.jqwong.music.model.Call
import com.jqwong.music.model.Lyric
import com.jqwong.music.model.MediaStatus
import com.jqwong.music.service.KuWoService
import com.jqwong.music.utils.AudioPlayerUtil
import com.jqwong.music.view.layoutManager.CenterLayoutManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LyricActivity : BaseActivity<ActivityLyricBinding>(){

    private var _touchSeek = false
    private var _autoScroll = true
    private val _adapter = LyricAdapter()

    override fun Title(): String {
        return Global.media!!.name
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        _binding.cpReturn.btnReturn.setOnClickListener {
            this.finish()
        }
        _binding.tvLyricNotFound.setOnClickListener {
            if(Global.media?.rid != null){
                it.visibility = View.INVISIBLE
                _binding.pbLoading.visibility = View.VISIBLE
                KuWoService().getLyric(Global.media!!.rid.toString(),object: Call<Lyric> {
                    override fun success(t: Lyric) {
                        t.data.lrclist.forEach {
                            it.inTime = (it.time.toFloat() * 1000).toLong()
                        }
                        Global.lyricIndex = 0
                        Global.lyric = t
                        EventBus.getDefault().post(LyricLoadEvent(LyricLoadStatus.SUCCESS,t))
                    }

                    override fun error(e: Throwable) {
                        toast(e.message.toString())
                        it.visibility = View.VISIBLE
                        _binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
            }
        }

        _binding.btnNext.setOnClickListener {
            AudioPlayerUtil.next()
        }

        _binding.btnPre.setOnClickListener {
            AudioPlayerUtil.previous()
        }

        _binding.btnPlay.setOnClickListener {
            AudioPlayerUtil.changeStatus()
        }

        _binding.sbPosition.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                _touchSeek = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val position:Long = (Global.media!!.time * (p0?.progress!! / 100.0)).toLong()
                AudioPlayerUtil.seekToPosition(position)
                _touchSeek = false
                if(Global.lyric?.data != null){
                    val index = Global.lyric?.data!!.FindCurrentIndex(position)
                    _binding.rvList.layoutManager.let {
                        it?.scrollToPosition(index)
                    }
                }
            }
        })

        _binding.btnPlay.setOnLongClickListener {
            true
        }
    }

    override fun initView() {
        val manager = CenterLayoutManager(this)
        manager.orientation = (RecyclerView.VERTICAL)
        _binding.rvList.layoutManager = manager
        _binding.rvList.adapter = _adapter
        if(Global.lyric?.data?.lrclist != null){
            _adapter.submitList(Global.lyric!!.data.lrclist)
            _adapter.notifyDataSetChanged()
            manager.scrollToPosition(Global.lyricIndex)
        }
        else{
            _binding.tvLyricNotFound.visibility = View.VISIBLE
        }


        if(Global.media?.name != null){
            _binding.cpTitle.tvTitle.text = Global.media?.name
        }

        _binding.btnPlay.setImageResource(if(AudioPlayerUtil.getPlayerStatus() == MediaStatus.PLAYING) R.drawable.ic_pause else R.drawable.ic_playon)
    }

    override fun useEventBus(): Boolean {
        return true
    }


    /**
     * 歌词改变事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricIndexChangeEvent(event:LyricIndexChangeEvent){
        if(Global.lyric?.data == null){
            return
        }
        if(_autoScroll){
            _binding.rvList.layoutManager?.let {
                it.smoothScrollToPosition(_binding.rvList,RecyclerView.State(),event.index)
            }
        }
        _adapter.notifyDataSetChanged()
    }

    /**
     * 歌词加载事件
     * @param event LyricLoadEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricLoadEvent(event:LyricLoadEvent){
        when(event.status){
            LyricLoadStatus.LOADING -> {
                _adapter.items = listOf()
                _adapter.notifyDataSetChanged()
                _binding.tvLyricNotFound.visibility = View.INVISIBLE
                _binding.pbLoading.visibility = View.VISIBLE
            }
            LyricLoadStatus.ERROR -> {
                _binding.tvLyricNotFound.visibility = View.VISIBLE
                _binding.pbLoading.visibility = View.INVISIBLE
                _adapter.items = listOf()
                _adapter.notifyDataSetChanged()
            }
            LyricLoadStatus.SUCCESS -> {
                if(event.lyric != null){
                    _binding.tvLyricNotFound.visibility = View.INVISIBLE
                    _binding.pbLoading.visibility = View.INVISIBLE
                    _adapter.items = listOf()
                    _adapter.addAll(event.lyric.data.lrclist)
                    _adapter.notifyDataSetChanged()
                    _binding.rvList.layoutManager.let {
                        it?.scrollToPosition(0)
                    }
                }
            }
        }
    }


    /**
     * 播放进度改变事件
     * @param event MediaPositionChangeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPositionChangeEvent(event:MediaPositionChangeEvent){
        if(!_touchSeek && Global.media != null && Global.media?.time != "0".toLong()){
            val process = (event.position * 1.0 /Global.media!!.time * 100).toInt()
            _binding.sbPosition.progress = process
        }
    }

    /**
     * 播放器状态改变事件
     * @param event PlayerStatusChangeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStatusChangeEvent(event:PlayerStatusChangeEvent){
        _binding.btnPlay.setImageResource(if(event.status == MediaStatus.PLAYING) R.drawable.ic_pause else R.drawable.ic_playon)
    }

    /**
     * 歌曲改变事件
     * @param event MediaChangeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event:MediaChangeEvent){
        _binding.cpTitle.tvTitle.text = event.media.name
    }
}