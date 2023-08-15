package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
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
import com.jqwong.music.helper.*
import com.jqwong.music.model.*
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.*

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
            supportActionBar?.title = currentSheet.name
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
        menuInflater.inflate(R.menu.menu_recommend_sheet,menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        if(_binding.includeMain.stateLayout.loaded) {
            when (item.itemId) {
                R.id.action_refresh -> {

                }
                R.id.action_change_platform -> {
                    changePlatform(listOf(Platform.KuWo,Platform.NetEaseCloud)) {
                        _binding.includeMain.stateLayout.showLoading()
                        _platform = it
                        if(sheets.containsKey(it)){
                            val item = sheets.get(it)!!.first()
                            page = 0
                            currentSheet = item
                            supportActionBar?.title = item.name
                            loadMediaList(_platform,item.id,0)
                        }
                        else{
                            getRecommendSheets(it, callback = {
                                sheets.put(_platform,it)
                                currentSheet = it.first()
                                page = 0
                                supportActionBar?.title = currentSheet.name
                                loadMediaList(_platform,currentSheet.id)
                            })
                        }
                    }
                }
                R.id.action_sheet -> {
                    MaterialDialog(this, BottomSheet()).show {
                        customView(R.layout.dialog_select_sheet)
                        cornerRadius(20f)
                        view.setBackgroundResource(R.drawable.bg_dialog)
                        view.setTitleDefaultStyle(this@RecommendSheetActivity)
                        val adapter = SongSheetAdapter()
                        val rvList = view.contentLayout.findViewById<RecyclerView>(R.id.rv_list)
                        rvList.layoutManager = LinearLayoutManager(this@RecommendSheetActivity)
                        rvList.adapter = adapter
                        adapter.submitList(sheets.get(_platform))
                        adapter.setOnItemClickListener(object:BaseQuickAdapter.OnItemClickListener<SongSheet>{
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onClick(
                                adapter: BaseQuickAdapter<SongSheet, *>,
                                view: View,
                                position: Int
                            ) {
                                val item = adapter.getItem(position)!!
                                page = 0
                                currentSheet = item
                                supportActionBar?.title = item.name
                                _binding.includeMain.stateLayout.showLoading()
                                loadMediaList(_platform,item.id,0)
                            }
                        })
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
                    reqParams = App.config.netEaseCloudMusicConfig.csrf_token.toString()
                }
                else -> {

                }
            }
            page++
            val data = ServiceProxy.getRecommendSongSheetData(platform,id,page,pageItemSize,reqParams)
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == maxReloadCount){
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
                    reqParams = App.config.netEaseCloudMusicConfig.csrf_token!!
                }
                else -> {

                }
            }
            val data = ServiceProxy.getRecommendSongSheetList(platform,reqParams)
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == maxReloadCount){
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