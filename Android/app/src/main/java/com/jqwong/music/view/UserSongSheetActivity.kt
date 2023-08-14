package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.helper.setErrorInfo
import com.jqwong.music.helper.startAnimation
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
        supportActionBar?.title = songSheet.name
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
            var reqParams:Any = ""
            when(_platform){
                Platform.NetEaseCloud -> {
                    reqParams = "${songSheet.id};${_params}"
                }
                else -> {}
            }
            val result = ServiceProxy.getUserSheetData(_platform,page,pageItemSize,reqParams)
            withContext(Dispatchers.Main){
                if(result.exception != null){
                    if(reloadNumber == maxReloadCount){
                        toast(result.message)
                        _binding.includeMain.stateLayout.apply {
                            onError {
                                this@apply.startAnimation()
                            }
                            this.showError()
                            this.setErrorInfo(result.exception)
                        }
                    }
                    else{
                        page--
                        loadMediaList(reloadNumber+1)
                    }
                }
                else{
                    if(page == 1){
                        adapter.submitList(result.data)
                        _binding.includeMain.stateLayout.apply {
                            onContent {
                                this@apply.startAnimation()
                            }
                            this.showContent()
                        }
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