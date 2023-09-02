package com.jqwong.music.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
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
import com.jqwong.music.adapter.LeaderBoardAdapter
import com.jqwong.music.app.App
import com.jqwong.music.helper.*
import com.jqwong.music.model.*
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardActivity:Template() {
    private val leaderboards:MutableMap<Platform,List<Leaderboard>> = mutableMapOf()
    private lateinit var currentLeaderboard:Leaderboard

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        supportActionBar?.title = "排行榜"
            intent.getStringExtra(ExtraKey.Platform.name).let {
            if(it == null || it == ""){
                toast("platform is null")
                finish()
            }
            _platform = Platform.valueOf(it!!)
        }
        _binding.includeMain.stateLayout.showLoading()
        getLeaderBoards(_platform, callback = {
            leaderboards.put(_platform,it)
            currentLeaderboard = getFirstLeaderboard(it)
            supportActionBar?.subtitle = currentLeaderboard.name
            loadMediaList(_platform,currentLeaderboard.id!!)
        })
    }
    override fun intView() {
        super.intView()
        adapterHelper.trailingLoadStateAdapter?.setOnLoadMoreListener(object: TrailingLoadStateAdapter.OnTrailingListener{
            override fun onFailRetry() {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLoad() {
                loadMediaList(_platform,currentLeaderboard.id.toString())
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_leaderboard,menu)
        return true
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(_binding.includeMain.stateLayout.loaded){
            when(item.itemId) {
                R.id.action_leaderboard -> {
                    MaterialDialog(this, BottomSheet()).show {
                        customView(R.layout.dialog_select_common_x)
                        cornerRadius(20f)
                        view.setBackgroundResource(R.drawable.bg_dialog)
                        view.setTitleDefaultStyle(this@LeaderboardActivity)
                        val adapter = LeaderBoardAdapter()
                        val rvList = view.contentLayout.findViewById<RecyclerView>(R.id.rv_list)
                        val path = mutableListOf<Leaderboard>()
                        rvList.layoutManager = LinearLayoutManager(this@LeaderboardActivity)
                        rvList.adapter = adapter
                        adapter.setOnItemClickListener(object:BaseQuickAdapter.OnItemClickListener<Leaderboard>{
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onClick(
                                adapter: BaseQuickAdapter<Leaderboard, *>,
                                view: View,
                                position: Int
                            ) {
                                val item = adapter.getItem(position)!!
                                path.add(item)
                                if(item.children != null){
                                    adapter.submitList(item.children)
                                }
                                else{
                                    page = 0
                                    currentLeaderboard = item
                                    supportActionBar?.subtitle = currentLeaderboard.name
                                    _binding.includeMain.stateLayout.showLoading()
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        loadMediaList(_platform,item.id.toString(),0)
                                    }
                                }
                            }

                        })
                        adapter.submitList(leaderboards.get(_platform))
                        val btnPoP = view.contentLayout.findViewById<ImageButton>(R.id.btn_pop)
                        btnPoP.setOnClickListener {
                            if(path.size > 0){
                                path.removeAt(path.size-1)
                                if(path.size == 0){
                                    adapter.submitList(leaderboards.get(_platform))
                                }
                                else{
                                    adapter.submitList(path.get(path.size-1).children)
                                }
                            }
                            else{
                                this.cancel()
                            }
                        }
                    }
                }
                R.id.action_change_platform -> {
                    changePlatform(listOf(Platform.KuWo,Platform.NetEaseCloud)) {
                        if(it == _platform)
                            return@changePlatform
                        page = 0
                        _platform = it
                        _binding.includeMain.stateLayout.showLoading()
                        if(leaderboards.containsKey(_platform)){
                            currentLeaderboard = getFirstLeaderboard(leaderboards.get(_platform)!!)
                            supportActionBar?.subtitle = currentLeaderboard.name
                            loadMediaList(_platform,currentLeaderboard.id!!)
                        }
                        else{
                            supportActionBar?.subtitle = ""
                            _binding.includeToolbar.cpiLoading.visibility = View.VISIBLE
                            getLeaderBoards(_platform,{
                                _binding.includeToolbar.cpiLoading.visibility = View.GONE
                                leaderboards.put(_platform,it)
                                currentLeaderboard = getFirstLeaderboard(leaderboards.get(_platform)!!)
                                supportActionBar?.subtitle = currentLeaderboard.name
                                loadMediaList(_platform,currentLeaderboard.id!!)
                            })
                        }
                    }
                }
                R.id.action_lyric -> {
                    gotoLyricActivity()
                }
                R.id.action_refresh -> {
                    _binding.includeMain.stateLayout.showLoading()
                    page = 0
                    loadMediaList(_platform,currentLeaderboard.id!!)
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
    private fun getFirstLeaderboard(data:List<Leaderboard>):Leaderboard{
        val lb = data.first()
        return if(lb.children != null){
            getFirstLeaderboard(lb.children)
        } else{
            lb
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLeaderBoards(platform: Platform, callback:(List<Leaderboard>) -> Unit, reloadNumber: Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay((1000 * reloadNumber).toLong())
            }
            val data = ServiceProxy.get(platform).data?.getLeaderboard()!!
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == App.config.retry_max_count){
                        _binding.includeMain.stateLayout.apply {
                            onError {
                                this@apply.startAnimation()
                            }
                            this.showError()
                            this.error(data.exception)
                        }
                    }
                    else{
                        getLeaderBoards(platform,callback,reloadNumber+1)
                    }
                }
                else{
                    val result = data.data!!
                    callback(result)
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMediaList(platform: Platform, id:String, reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay((1000 * reloadNumber).toLong())
            }
            page++
            val data = ServiceProxy.get(platform).data?.getLeaderboardSongList(id,page,pageItemSize)!!
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

}