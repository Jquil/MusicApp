package com.jqwong.music.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.R
import com.jqwong.music.app.App
import com.jqwong.music.helper.*
import com.jqwong.music.model.*
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author: Jq
 * @date: 7/29/2023
 */
class SearchResultActivity:Template() {
    private lateinit var key:String
    
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        supportActionBar?.title = "搜索"
        intent.getStringExtra(ExtraKey.Search.name).let {
            if(it == null || it == ""){
                toast("search key is null")
                finish()
            }
            key = it.toString()
            supportActionBar?.subtitle = key
        }
        intent.getStringExtra(ExtraKey.Platform.name).let {
            if(it == null || it == ""){
                toast("platform is null")
                finish()
            }
            _platform = Platform.valueOf(it!!)
        }
        _binding.includeMain.stateLayout.showLoading()
        loadData()
    }
    override fun intView() {
        super.intView()
        adapterHelper.trailingLoadStateAdapter?.setOnLoadMoreListener(object: TrailingLoadStateAdapter.OnTrailingListener{
            override fun onFailRetry() {

            }

            
            override fun onLoad() {
                loadData()
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        return true
    }

    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_refresh -> {
                _binding.includeMain.stateLayout.showLoading()
                page = 0
                loadData()
            }
            R.id.action_lyric -> {
                gotoLyricActivity()
            }
            R.id.action_change_platform -> {
                changePlatform(){
                    if(it == _platform)
                        return@changePlatform
                    _platform = it
                    page = 0
                    _binding.includeMain.stateLayout.showLoading()
                    loadData()
                }
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
        menuInflater.inflate(R.menu.menu_search_item,menu)
    }
    
    private fun loadData(reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay(1000)
            }
            page++
            val data = ServiceProxy.get(_platform).data?.search(key,page,pageItemSize)!!
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == App.config.retry_max_count){
                        toast(data.message)
                        _binding.includeMain.stateLayout.error(data.exception)
                    }
                    else{
                        page--
                        loadData(reloadNumber+1)
                    }
                }
                else{
                    if(!data.support){
                        _binding.includeMain.stateLayout.empty("暂不支持'${_platform.toString()}'搜索噢")
                        return@withContext
                    }
                    if(!data.success){
                        _binding.includeMain.stateLayout.empty(data.message)
                        return@withContext
                    }

                    if(page == 1){
                        adapter.submitList(data.data)
                        _binding.includeMain.stateLayout.content()
                    }
                    else{
                        adapter.addAll(data.data!!)
                    }

                    if(data.data!!.size < pageItemSize){
                        loadFinish = true
                    }
                    adapterHelper.trailingLoadState = LoadState.NotLoading(loadFinish)
                }
            }
        }
    }
}