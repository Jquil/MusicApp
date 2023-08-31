package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.R
import com.jqwong.music.adapter.SongSheetAdapter
import com.jqwong.music.app.App
import com.jqwong.music.event.CollectOrCancelMediaEvent
import com.jqwong.music.helper.*
import com.jqwong.music.model.*
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

/**
 * @author: Jq
 * @date: 8/12/2023
 */
class RecommendSheetActivity:Template() {

    private val sheets = mutableMapOf<Platform,List<SongSheet>>()
    private lateinit var currentSheet: SongSheet

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        supportActionBar?.title = "每日推荐歌单"
        intent.getStringExtra(ExtraKey.Platform.name).let {
            if(it == null || it == ""){
                toast("platform is null")
                finish()
            }
            _platform = Platform.valueOf(it!!)
        }
        _binding.includeMain.stateLayout.showLoading()
        getRecommendSheets(_platform, callback = {
            sheets.put(_platform,it)
            currentSheet = it.first()
            supportActionBar?.subtitle = currentSheet.name
            loadMediaList(_platform,currentSheet.id)
        })
    }

    override fun intView() {
        super.intView()
        adapterHelper.trailingLoadStateAdapter?.setOnLoadMoreListener(object: TrailingLoadStateAdapter.OnTrailingListener{
            override fun onFailRetry() {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLoad() {
                loadMediaList(_platform,currentSheet.id)
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sheet,menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        if(_binding.includeMain.stateLayout.loaded) {
            when (item.itemId) {
                R.id.action_lyric -> {
                    gotoLyricActivity()
                }
                R.id.action_refresh -> {
                    _binding.includeMain.stateLayout.showLoading()
                    page = 0
                    loadMediaList(_platform,currentSheet.id)
                }
                R.id.action_change_platform -> {
                    changePlatform(listOf(Platform.KuWo,Platform.NetEaseCloud)) {
                        _binding.includeMain.stateLayout.showLoading()
                        _platform = it
                        if(sheets.containsKey(it)){
                            val item = sheets.get(it)!!.first()
                            page = 0
                            currentSheet = item
                            supportActionBar?.subtitle = item.name
                            loadMediaList(_platform,item.id,0)
                        }
                        else{
                            getRecommendSheets(it, callback = {
                                sheets.put(_platform,it)
                                currentSheet = it.first()
                                page = 0
                                supportActionBar?.subtitle = currentSheet.name
                                loadMediaList(_platform,currentSheet.id)
                            })
                        }
                    }
                }
                R.id.action_sheet -> {
                    selectSheet(sheets.get(_platform)!!){
                        page = 0
                        currentSheet = it
                        supportActionBar?.subtitle = it.name
                        _binding.includeMain.stateLayout.showLoading()
                        loadMediaList(_platform,it.id,0)
                    }
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
        menuInflater.inflate(R.menu.menu_sheet_item,menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMediaList(platform: Platform, id:String, reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay((1000 * reloadNumber).toLong())
            }
            var reqParams:Any = ""
            when(platform){
                Platform.NetEaseCloud -> {
                    reqParams = App.config.netEaseCloudConfig.csrf_token.toString()
                }
                else -> {

                }
            }
            page++
            val data = ServiceProxy.getService(platform).data?.getRecommendSongSheetData(id,page,pageItemSize,reqParams)!!
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == App.config.retry_max_count){
                        toast(data.message)
                        _binding.includeMain.stateLayout.error(data.exception)
                    }
                    else{
                        page--
                        loadMediaList(platform,id,reloadNumber+1)
                    }
                }
                else{
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRecommendSheets(platform: Platform, callback:(List<SongSheet>) -> Unit, reloadNumber: Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay((1000 * reloadNumber).toLong())
            }
            var reqParams:Any = ""
            when(platform){
                Platform.NetEaseCloud->{
                    reqParams = App.config.netEaseCloudConfig.csrf_token 
                }
                else -> {

                }
            }
            val data = ServiceProxy.getService(platform).data?.getRecommendSongSheetList(reqParams)!!
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == App.config.retry_max_count){
                        _binding.includeMain.stateLayout.error(data.exception)
                    }
                    else{
                        getRecommendSheets(platform,callback,reloadNumber+1)
                    }
                }
                else{
                    val result = data.data!!
                    callback(result)
                }
            }
        }
    }
}