package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.helper.content
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

    @RequiresApi(Build.VERSION_CODES.O)
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

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLoad() {
                loadMediaList()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMediaList(reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay((1000 * reloadNumber).toLong())
            }
            page++

            val result = ServiceProxy.getArtistSongList(_platform,artist.id,page,pageItemSize)
            withContext(Dispatchers.Main){
                if(result.exception != null){
                    if(reloadNumber == maxReloadCount){
                        toast(result.message)
                        _binding.includeMain.stateLayout.error(result.exception)
                    }
                    else{
                        page--
                        loadMediaList(reloadNumber+1)
                    }
                }
                else{
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
}