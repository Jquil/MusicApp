package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.jqwong.music.R
import com.jqwong.music.adapter.MediaAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivitySearchResultBinding
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.model.ExceptionLog
import com.jqwong.music.model.ExtraKey
import com.jqwong.music.model.Platform
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author: Jq
 * @date: 7/29/2023
 */
class SearchResultActivity:BaseActivity<ActivitySearchResultBinding>() {
    private lateinit var key:String
    private lateinit var platform:Platform
    private lateinit var adapter:MediaAdapter
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
                    message = e.message.toString(),
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
        _binding.includeToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.includeMain.rvList.layoutManager = LinearLayoutManager(this)
        adapter = MediaAdapter()
        _binding.includeMain.rvList.adapter = adapter
    }
    override fun useEventBus(): Boolean {
        return false
    }
    override fun statusBarColor(): Int {
        return R.color.background
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_result,menu)
        return true
    }
    private fun loadData(reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            page++
            val data = ServiceProxy.Search(platform,key,page,pageItemSize)
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    toast(data.exception.message)
                    if(reloadNumber == maxReloadCount){
                        // show error info
                    }
                    else{
                        loadData(reloadNumber+1)
                    }
                }
                else{
                    if(page == 1){
                        adapter.submitList(data.data)
                        _binding.includeMain.stateLayout.showContent()
                    }
                    else{
                        adapter.addAll(data.data!!)
                    }
                }
            }
        }
    }
}