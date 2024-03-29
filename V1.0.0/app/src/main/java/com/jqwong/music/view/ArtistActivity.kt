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
import com.jqwong.music.model.Artist
import com.jqwong.music.model.ExtraKey
import com.jqwong.music.model.Platform
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistActivity:Template() {

    private lateinit var artist: Artist

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        intent.getStringExtra(ExtraKey.Artist.name).let {
            if(it == null || it == ""){
                toast("artist is null")
                finish()
            }
            artist = Artist.fromJson(it!!)
            _platform = artist.platform
        }
        var name = artist.name
        if(artist.platform == Platform.KuWo && name.contains('&')){
            val arr = name.split('&')
            name = arr.first()
        }
        supportActionBar?.title = name
        _binding.includeMain.stateLayout.showLoading()
        loadMediaList()
    }

    override fun intView() {
        super.intView()
        adapterHelper.trailingLoadStateAdapter?.setOnLoadMoreListener(object: TrailingLoadStateAdapter.OnTrailingListener{
            override fun onFailRetry() {

            }


            override fun onLoad() {
                loadMediaList()
            }
        })
    }


    private fun loadMediaList(reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay((1000 * reloadNumber).toLong())
            }
            page++

            var id = artist.id
            if(_platform == Platform.QQ && artist.data.containsKey("mid")){
                id = artist.data.get("mid").toString()
            }
            val result = ServiceProxy.get(_platform).data?.getArtistSongList(id,page,pageItemSize)!!
            withContext(Dispatchers.Main){
                if(result.exception != null){
                    if(reloadNumber == App.config.retry_max_count){
                        toast(result.message)
                        _binding.includeMain.stateLayout.error(result.exception)
                    }
                    else{
                        page--
                        loadMediaList(reloadNumber+1)
                    }
                }
                else{
                    if(!result.support){
                        _binding.includeMain.stateLayout.empty(if(result.message.isNullOrEmpty()) "暂不支持获取'${_platform.toString()}'平台歌手的歌曲列表" else result.message)
                        return@withContext
                    }
                    if(!result.success){
                        _binding.includeMain.stateLayout.empty("请求失败啦")
                        return@withContext
                    }
                    if(page == 1){
                        adapter.submitList(result.data)
                        _binding.includeMain.stateLayout.content()
                    }
                    else{
                        adapter.addAll(result.data!!)
                    }

                    if(result.data!!.size < pageItemSize){
                        loadFinish = true
                    }
                    adapterHelper.trailingLoadState = LoadState.NotLoading(loadFinish)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_artist,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_change_platform->{
                changePlatform(_platform,artist.name)
            }
            R.id.action_lyric->{
                gotoLyricActivity()
            }
            R.id.action_collect->{

            }
            R.id.action_refresh->{
                page=0
                _binding.includeMain.stateLayout.showLoading()
                loadMediaList()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_artist_item,menu)
    }


}