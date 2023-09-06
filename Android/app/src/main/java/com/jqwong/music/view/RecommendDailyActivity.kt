package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.R
import com.jqwong.music.app.App
import com.jqwong.music.helper.content
import com.jqwong.music.helper.empty
import com.jqwong.music.helper.error
import com.jqwong.music.model.ExtraKey
import com.jqwong.music.model.Platform
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecommendDailyActivity:Template() {
    
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        _platform = Platform.valueOf(intent.getStringExtra(ExtraKey.Platform.name)!!)
        supportActionBar?.title = "每日推荐歌曲"
        _binding.includeMain.stateLayout.showLoading()
        loadMediaList(_platform)
    }

    override fun intView() {
        super.intView()
        adapterHelper.trailingLoadStateAdapter?.setOnLoadMoreListener(object: TrailingLoadStateAdapter.OnTrailingListener{
            override fun onFailRetry() {

            }

            
            override fun onLoad() {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recommend_daily,menu)
        return true
    }

    
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.action_lyric -> {
                gotoLyricActivity()
            }
            R.id.action_change_platform -> {
                changePlatform(listOf()) {
                    if (it == _platform)
                        return@changePlatform
                    _binding.includeMain.stateLayout.showLoading()
                    _platform = it
                    loadMediaList(_platform)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    
    private fun loadMediaList(platform: Platform,reloadNumber: Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay((1000 * reloadNumber).toLong())
            }
            var reqParams:Any = ""
            when(platform){
                Platform.NetEaseCloud -> {
                    reqParams = App.config.netEaseCloudConfig.csrf_token
                }
                else -> {}
            }
            page++
            val data = ServiceProxy.get(platform).data?.getRecommendDaily(reqParams)!!
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == App.config.retry_max_count){
                        _binding.includeMain.stateLayout.error(data.exception)
                    }
                    else{
                        loadMediaList(platform,reloadNumber+1)
                    }
                }
                else{
                    if(!data.support){
                        _binding.includeMain.stateLayout.empty("暂不支持'${platform.toString()}'获取每日推荐歌曲噢")
                        return@withContext
                    }
                    if(!data.success){
                        _binding.includeMain.stateLayout.empty(data.message)
                        return@withContext
                    }
                    else{
                        if(page == 1){
                            adapter.submitList(data.data)
                            _binding.includeMain.stateLayout.content()
                        }
                        else{
                            adapter.addAll(data.data!!)
                        }
                        loadFinish = true
                        adapterHelper.trailingLoadState = LoadState.NotLoading(loadFinish)
                    }
                }
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_sheet_item,menu)
    }

    
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_artist -> {
                gotoArtistActivity(adapter.getSelectMediaByLongClick())
            }
            R.id.action_collect -> {
                val media = adapter.getSelectMediaByLongClick() ?: return false
                collectOrCancelMedia(_platform,null,media,true){}
            }
            R.id.action_change_platform -> {
                val media = adapter.getSelectMediaByLongClick() ?: return false
                changePlatform(_platform,media.name)
            }
        }
        return super.onContextItemSelected(item)
    }
}