package com.jqwong.music.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.R
import com.jqwong.music.adapter.CustomLoadMoreAdapter
import com.jqwong.music.adapter.LeaderBoardAdapter
import com.jqwong.music.adapter.MediaAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityLeaderboardBinding
import com.jqwong.music.event.MediaChangeEvent
import com.jqwong.music.event.MediaLoadingEvent
import com.jqwong.music.helper.*
import com.jqwong.music.model.*
import com.jqwong.music.service.ServiceProxy
import com.jqwong.music.view.listener.DoubleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LeaderboardActivity:BaseActivity<ActivityLeaderboardBinding>() {
    private lateinit var _platform: Platform
    private lateinit var adapter:MediaAdapter
    private lateinit var adapterHelper: QuickAdapterHelper
    private var loadFinish:Boolean = false
    private var page:Int = 0
    private val leaderboards:MutableMap<Platform,List<Leaderboard>> = mutableMapOf()
    private lateinit var currentLeaderboard:Leaderboard

    override fun initData(savedInstanceState: Bundle?) {
        intent.getStringExtra(ExtraKey.Platform.name).let {
            if(it == null || it == ""){
                toast("platform is null")
                finish()
            }
            try {
                _platform = Platform.valueOf(it!!)
            }
            catch (e:Exception){
                val log = ExceptionLog(
                    title = "Leaderboard page get 'Platform' failed",
                    exception = e,
                    time = TimeHelper.getTime()
                )
                App.exceptions.add(log)
                toast(log.title)
                finish()
            }
        }
        _binding.includeMain.stateLayout.showLoading()
        loadLeaderBoard(_platform, callback = {
            val first = getFirstLeaderboard(it)
            supportActionBar?.title = first.name
            loadMediaList(_platform,first.id!!)
        })
    }

    override fun intView() {
        setSupportActionBar(_binding.includeToolbar.toolbar)
        _binding.includeToolbar.toolbar.setOnLongClickListener {
            true
        }
        _binding.includeToolbar.toolbar.setOnClickListener(object: DoubleClickListener(){
                override fun onDoubleClick(v: View?) {
                _binding.includeMain.rvList.scrollToPosition(0)
            }
        })
        _binding.includeToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.includeMain.rvList.layoutManager = LinearLayoutManager(this)
        adapter = MediaAdapter()
        adapter.setOnItemClickListener(@UnstableApi object: BaseQuickAdapter.OnItemClickListener<Media>{
            override fun onClick(adapter: BaseQuickAdapter<Media, *>, view: View, position: Int) {
                App.playList = PlayList(0,null,adapter.items.subList(position,adapter.items.size).copy())
                adapter.notifyDataSetChanged()
                AudioHelper.start()
            }
        })
        val loadMoreAdapter = CustomLoadMoreAdapter()
        loadMoreAdapter.setOnLoadMoreListener(object: TrailingLoadStateAdapter.OnTrailingListener{
            override fun onFailRetry() {

            }

            override fun onLoad() {
                loadMediaList(_platform,currentLeaderboard.id.toString())
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
        menuInflater.inflate(R.menu.menu_leaderboard,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_leaderboard -> {
                MaterialDialog(this, BottomSheet()).show {
                    customView(R.layout.dialog_select_leaderboard)
                    cornerRadius(20f)
                    view.setBackgroundResource(R.drawable.bg_dialog)
                    view.setTitleDefaultStyle(this@LeaderboardActivity)
                    val adapter = LeaderBoardAdapter()
                    val rvList = view.contentLayout.findViewById<RecyclerView>(R.id.rv_list)
                    val path = mutableListOf<Leaderboard>()
                    rvList.layoutManager = LinearLayoutManager(this@LeaderboardActivity)
                    rvList.adapter = adapter
                    adapter.setOnItemClickListener(object:BaseQuickAdapter.OnItemClickListener<Leaderboard>{
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
                                supportActionBar?.title = item.name
                                _binding.includeMain.stateLayout.showLoading()
                                loadMediaList(_platform,item.id.toString(),0)
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getFirstLeaderboard(data:List<Leaderboard>):Leaderboard{
        currentLeaderboard = data.first()
        return if(currentLeaderboard.children != null){
            getFirstLeaderboard(currentLeaderboard.children!!)
        } else{
            currentLeaderboard
        }
    }

    private fun loadLeaderBoard(platform: Platform,callback:(List<Leaderboard>) -> Unit, reloadNumber: Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            val data = ServiceProxy.getLeaderboard(platform)
            withContext(Dispatchers.Main){
                if(data.exception != null){
                    if(reloadNumber == maxReloadCount){
                        _binding.includeMain.stateLayout.apply {
                            onError {
                                this@apply.startAnimation()
                            }
                            this.showError()
                            this.setErrorInfo(data.exception)
                        }
                    }
                    else{
                        loadLeaderBoard(platform,callback,reloadNumber+1)
                    }
                }
                else{
                    val result = data.data!!
                    leaderboards.put(platform,result)
                    callback(result)
                }
            }
        }
    }

    private fun loadMediaList(platform: Platform,id:String, reloadNumber:Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            if(reloadNumber != 0){
                delay(1000)
            }
            page++
            val data = ServiceProxy.getLeaderboardSongList(platform,id,page,pageItemSize)
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
                        loadMediaList(platform,id,reloadNumber+1)
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
    fun onMediaLoadingEvent(event:MediaLoadingEvent){
        _binding.includeToolbar.cpiLoading.visibility = if(event.finish) View.GONE else View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event: MediaChangeEvent){
        adapter.notifyDataSetChanged()
    }
}