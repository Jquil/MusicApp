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
import com.jqwong.music.helper.error
import com.jqwong.music.model.ExtraKey
import com.jqwong.music.model.Platform
import com.jqwong.music.model.SongSheet
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.*

/**
 * @author: Jq
 * @date: 8/13/2023
 */
class UserSongSheetActivity:Template() {

    private lateinit var songSheet:SongSheet
    private lateinit var _params:Any

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        supportActionBar?.title = "我的歌单"
        intent.getStringExtra(ExtraKey.Platform.name).let {
            if(it == null || it == ""){
                toast("platform is null")
                finish()
            }
            _platform = Platform.valueOf(it!!)
        }
        intent.getStringExtra(ExtraKey.SongSheet.name).let {
            if(it == null || it == ""){
                toast("platform is null")
                finish()
            }
            songSheet = SongSheet.fromJsom(it!!)
        }
        intent.getStringExtra(ExtraKey.Data.name).let{
            _params = it!!
        }
        supportActionBar?.subtitle = songSheet.name
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
            var reqParams:Any = ""
            when(_platform){
                Platform.NetEaseCloud -> {
                    reqParams = "${songSheet.id};${_params}"
                }
                else -> {}
            }
            val result = ServiceProxy.get(_platform).data?.getUserSheetData(page,pageItemSize,reqParams)!!
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
        menuInflater.inflate(R.menu.menu_sheet_user,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.action_lyric->{
                gotoLyricActivity()
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
        menuInflater.inflate(R.menu.menu_sheet_user_item,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_artist -> {
                gotoArtistActivity(adapter.getSelectMediaByLongClick())
            }
            R.id.action_remove -> {
                val media = adapter.getSelectMediaByLongClick() ?: return false
                collectOrCancelMedia(_platform,songSheet,media,false){
                    if(it){
                        adapter.remove(media)
                    }
                }
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