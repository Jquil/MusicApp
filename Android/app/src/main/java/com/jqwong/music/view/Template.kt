package com.jqwong.music.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.jqwong.music.R
import com.jqwong.music.adapter.CustomLoadMoreAdapter
import com.jqwong.music.adapter.MediaAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityTemplateBinding
import com.jqwong.music.event.CollectOrCancelMediaEvent
import com.jqwong.music.event.MediaChangeEvent
import com.jqwong.music.event.MediaLoadingEvent
import com.jqwong.music.helper.AudioHelper
import com.jqwong.music.model.Artist
import com.jqwong.music.model.ExtraKey
import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.jqwong.music.model.PlayList
import com.jqwong.music.model.copy
import com.jqwong.music.view.listener.DoubleClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: Jq
 * @date: 8/13/2023
 */
abstract class Template:BaseActivity<ActivityTemplateBinding>() {
    lateinit var _platform: Platform
    lateinit var adapter: MediaAdapter
    lateinit var adapterHelper: QuickAdapterHelper
    var loadFinish:Boolean = false
    var page:Int = 0

    override fun initData(savedInstanceState: Bundle?) {

    }
    override fun intView() {
        setSupportActionBar(_binding.includeToolbar.toolbar)
        _binding.includeToolbar.toolbar.setOnLongClickListener {
            true
        }
        _binding.includeToolbar.toolbar.setOnClickListener(object: DoubleClickListener(){
            override fun onDoubleClick(v: View?) {
                _binding.includeMain.rvList.scrollToPosition(0)
            }
        })
        _binding.includeToolbar.toolbar.setNavigationOnClickListener(object:OnClickListener{
            override fun onClick(v: View?) {
                finish()
            }

        })
        _binding.includeMain.rvList.layoutManager = LinearLayoutManager(this)
        adapter = MediaAdapter()
        adapter.setOnItemClickListener(@UnstableApi object: BaseQuickAdapter.OnItemClickListener<Media>{
            @SuppressLint("NewApi")
            override fun onClick(adapter: BaseQuickAdapter<Media, *>, view: View, position: Int) {
                App.playList = PlayList(0,null,adapter.items.subList(position,adapter.items.size).copy())
                adapter.notifyDataSetChanged()
                AudioHelper.start()
            }
        })

        val loadMoreAdapter = CustomLoadMoreAdapter()
        adapterHelper = QuickAdapterHelper.Builder(adapter)
            .setTrailingLoadStateAdapter(loadMoreAdapter)
            .build()
        _binding.includeMain.rvList.adapter = adapterHelper.adapter
        registerForContextMenu(_binding.includeMain.rvList)
    }
    override fun useEventBus(): Boolean {
        return true
    }
    override fun statusBarColor(): Int {
        return R.color.background
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterForContextMenu(_binding.includeMain.rvList)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event: MediaChangeEvent){
        adapter.notifyDataSetChanged()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaLoadingEvent(event: MediaLoadingEvent){
        _binding.includeToolbar.cpiLoading.visibility = if(event.finish) View.GONE else View.VISIBLE
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCollectOrCancelMediaEvent(event: CollectOrCancelMediaEvent){
        _binding.includeToolbar.cpiLoading.visibility = if(event.finish) View.GONE else View.VISIBLE
    }
}