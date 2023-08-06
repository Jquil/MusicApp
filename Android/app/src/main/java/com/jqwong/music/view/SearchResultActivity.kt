package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.R
import com.jqwong.music.adapter.CustomLoadMoreAdapter
import com.jqwong.music.adapter.MediaAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivitySearchResultBinding
import com.jqwong.music.event.MediaChangeEvent
import com.jqwong.music.helper.*
import com.jqwong.music.model.*
import com.jqwong.music.service.ServiceProxy
import com.jqwong.music.view.listener.DoubleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: Jq
 * @date: 7/29/2023
 */
class SearchResultActivity:BaseActivity<ActivitySearchResultBinding>() {
    private lateinit var key:String
    private lateinit var platform:Platform
    private lateinit var adapter:MediaAdapter
    private lateinit var adapterHelper: QuickAdapterHelper
    private var loadFinish:Boolean = false
    private var page:Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData(savedInstanceState: Bundle?) {
        intent.getStringExtra(ExtraKey.Search.name).let {
            if(it == null || it == ""){
                toast("search key is null")
                finish()
            }
            key = it.toString()
            supportActionBar?.title = key
        }
        intent.getStringExtra(ExtraKey.Platform.name).let {
            if(it == null || it == ""){
                toast("platform is null")
                finish()
            }
            try {
                platform = Platform.valueOf(it!!)
            }
            catch (e:Exception){
                val log = ExceptionLog(
                    title = "Search page get 'Platform' failed",
                    exception = e,
                    time = TimeHelper.getTime()
                )
                App.exceptions.add(log)
                toast(log.title)
                finish()
            }
        }
        _binding.includeMain.stateLayout.showLoading()
        loadData()
    }
    override fun intView() {
        setSupportActionBar(_binding.includeToolbar.toolbar)
        _binding.includeToolbar.toolbar.setOnLongClickListener {
            true
        }
        _binding.includeToolbar.toolbar.setOnClickListener(object:DoubleClickListener(){
            override fun onDoubleClick(v: View?) {
                _binding.includeMain.rvList.scrollToPosition(0)
            }
        })
        _binding.includeToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.includeMain.rvList.layoutManager = LinearLayoutManager(this)
        adapter = MediaAdapter()
        adapter.setOnItemClickListener(@UnstableApi object:BaseQuickAdapter.OnItemClickListener<Media>{
            override fun onClick(adapter: BaseQuickAdapter<Media, *>, view: View, position: Int) {
                App.playList = PlayList(0,null,adapter.items.subList(position,adapter.items.size).copy())
                adapter.notifyDataSetChanged()
                AudioHelper.start()
            }

        })
        val loadMoreAdapter = CustomLoadMoreAdapter()
        loadMoreAdapter.setOnLoadMoreListener(object:TrailingLoadStateAdapter.OnTrailingListener{
            override fun onFailRetry() {

            }

            override fun onLoad() {
                loadData()
            }

        })
        adapterHelper = QuickAdapterHelper.Builder(adapter)
            .setTrailingLoadStateAdapter(loadMoreAdapter)
            .build()
        _binding.includeMain.rvList.adapter = adapterHelper.adapter
    }
    override fun useEventBus(): Boolean {
        return true
    }
    override fun statusBarColor(): Int {
        return R.color.background
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_result,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_refresh -> {
                _binding.includeMain.stateLayout.showLoading()
                page = 0
                loadData()
            }
            R.id.action_change_platform -> {
                changePlatform(listOf(Platform.KuWo,Platform.NetEaseCloud)){
                    if(it == platform)
                        return@changePlatform
                    platform = it
                    page = 0
                    _binding.includeMain.stateLayout.showLoading()
                    loadData()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData(reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay(1000)
            }
            page++
            val data = ServiceProxy.search(platform,key,page,pageItemSize)
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == maxReloadCount){
                        toast(data.message)
                        _binding.includeMain.stateLayout.apply {
                            onError {
                                this@apply.startAnimation()
                            }
                            this.showError()
                            this.setErrorInfo(data.exception)
                        }
                    }
                    else{
                        page--
                        loadData(reloadNumber+1)
                    }
                }
                else{
                    if(page == 1){
                        adapter.submitList(data.data)
                        _binding.includeMain.stateLayout.apply {
                            onContent {
                                this@apply.startAnimation()
                            }
                            this.showContent()
                        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event: MediaChangeEvent){
        adapter.notifyDataSetChanged()
    }
}