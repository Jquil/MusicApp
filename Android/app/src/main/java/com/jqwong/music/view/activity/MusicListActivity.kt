package com.jqwong.music.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.OnItemChildClickListener
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.google.gson.Gson
import com.jqwong.music.R
import com.jqwong.music.adapter.MusicListAdapter
import com.jqwong.music.adapter.SheetChooseAdapter
import com.jqwong.music.app.App
import com.jqwong.music.app.Constant
import com.jqwong.music.app.Global
import com.jqwong.music.databinding.ActivityMusicListBinding
import com.jqwong.music.entity.FavoriteMedia
import com.jqwong.music.entity.Sheet
import com.jqwong.music.entity.SheetInfo
import com.jqwong.music.model.*
import com.jqwong.music.service.KuWoService
import com.jqwong.music.service.LocalDBService
import com.jqwong.music.service.MemfireDBService
import com.jqwong.music.utils.AudioPlayerUtil
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem

class MusicListActivity : BaseActivity<ActivityMusicListBinding>() {

    private lateinit var _params:MusicCondition
    private lateinit var _adapter:MusicListAdapter
    private lateinit var _artist:ArtistInfo
    private lateinit var _billboards:BillboardList
    private lateinit var _menuBillboard:PowerMenu
    private lateinit var _sheets:List<Sheet>
    private lateinit var _sheet:Sheet
    private lateinit var _menuArtist:PowerMenu
    private val _musicService = KuWoService()
    private val _localDBService = LocalDBService()
    private val _memfireDBService = MemfireDBService()
    private var _pagingInfo = PagingInfo(1,20,false,null)


    override fun Title(): String {
        return _params.title
    }

    override fun initData(savedInstanceState: Bundle?) {
        val objStr = intent.getStringExtra(Constant.ExtraKey)
        _params = Gson().fromJson(objStr,MusicCondition::class.java)
        prepare()
        loadSheets()
        onLoadData()
    }

    override fun initListener() {
        _binding.cpReturn.btnReturn.setOnClickListener {
            this.finish()
        }

        _binding.swlList.setOnRefreshListener {
            _pagingInfo.page = 1
            _pagingInfo.finish = false
            changeStatus(1,"")
            onLoadData()
            _adapter?.notifyDataSetChanged()
            _binding.swlList.isRefreshing = false
        }

        _adapter.setOnItemClickListener { adapter, view, position ->
            val list = adapter.items.subList(position,adapter.items.size)
            AudioPlayerUtil.play(list)
        }

        _adapter.setOnItemLongClickListener { adapter, view, position ->
            val item = adapter.getItem(position)
            buildMediaMenu(item!!).showAsAnchorRightTop(view)
            true
        }

        _binding.cpTitle.tvTitle.setOnLongClickListener {
            if(_params.key == MStatus.ARTIST){
                if(_menuArtist != null){
                    _menuArtist.showAsAnchorLeftBottom(_binding.cpTitle.tvTitle)
                }
            }
            else if (_params.key == MStatus.BILLBOARD){
                if(_menuBillboard != null){
                    _menuBillboard.showAsAnchorLeftBottom(_binding.cpTitle.tvTitle)
                }
            }
            true
        }
    }

    override fun initView() {
        _binding.rvList.layoutManager = LinearLayoutManager(this)
        _adapter = MusicListAdapter()
        _adapter.setEmptyViewLayout(this, R.layout.component_status)
        _adapter.isEmptyViewEnable = true
        _pagingInfo.helper = QuickAdapterHelper.Builder(_adapter)
            .setTrailingLoadStateAdapter(object: TrailingLoadStateAdapter.OnTrailingListener{
                override fun onFailRetry() {

                }

                override fun onLoad() {
                    onLoadData()
                }

            }).build()
        _binding.rvList.adapter = _pagingInfo.helper?.adapter
    }

    override fun useEventBus(): Boolean {
        return false
    }


    fun loadSheets(){
        val call = object:Call<List<Sheet>>{
            override fun success(data: List<Sheet>) {
                _sheets = data
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }
        }
        _localDBService.loadAll(call)
    }


    fun prepare(){
        when(_params.key){
            MStatus.ARTIST -> {
                loadArtistInfo()
                _menuArtist = buildArtistMenu()
            }
            MStatus.BILLBOARD -> {
                val _filter = "酷我"
                _billboards = Gson().fromJson(_params.value, BillboardList::class.java)
                _billboards.name = _billboards.name.replace(_filter,"")
                _billboards.list.forEach {
                    it.name = it.name.replace(_filter,"")
                }
                _menuBillboard = buildBillboardMenu(_billboards)
                _params.title = _billboards.list.get(0).name
            }
            MStatus.SHEET -> {
                _sheet = Gson().fromJson(_params.value, Sheet::class.java)
            }
            MStatus.SEARCH -> {

            }
            MStatus.FAVORITE -> {

            }
        }
    }


    /**
     * 构建音乐菜单
     * @param media Media
     * @return PowerMenu
     */
    fun buildMediaMenu(media: Media):PowerMenu{
        val FLAG_ARTIST = "Artist"
        val FLAG_FAVORITE = "Favorite"
        val FLAG_UN_FAVORITE = "UnFavorite"
        val FLAG_COLLECT = "Collect"
        val FLAG_UN_COLLECT = "UnCollect"
        val builder = PowerMenu.Builder(this)
        builder.addItem(PowerMenuItem(FLAG_ARTIST))
        if (_params.key == MStatus.FAVORITE){
            builder.addItem(PowerMenuItem(FLAG_UN_FAVORITE))
        }
        else{
            builder.addItem(PowerMenuItem(FLAG_FAVORITE))
        }
        if(_params.key == MStatus.SHEET){
            builder.addItem(PowerMenuItem(FLAG_UN_COLLECT))
        }
        else{
            builder.addItem(PowerMenuItem(FLAG_COLLECT))
        }
        builder.setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
        builder.setMenuRadius(10f)
        builder.setMenuShadow(10f)
        builder.setBackgroundAlpha(0f)
        val menu = builder.build()
        val eMedia = com.jqwong.music.entity.Media(null,media.musicrid,media.rid,media.artist,media.pic,media.album,media.albumid,media.name,media.pic120,media.songTimeMinutes,media.time,media.url,media.artistid)
        menu.setOnMenuItemClickListener { position, item ->
            when(item.title){
                FLAG_ARTIST -> {
                    val params = MusicCondition("",MStatus.ARTIST,media.artistid.toString())
                    startActivity(Intent(this,MusicListActivity::class.java).apply {
                        putExtra(Constant.ExtraKey, Gson().toJson(params))
                    })
                }
                FLAG_COLLECT -> {
                    MaterialDialog(this, BottomSheet()).show {
                        title(R.string.sheet)
                        customView(R.layout.component_rv)
                        cornerRadius(16f)
                        val rv = view.contentLayout.findViewById<RecyclerView>(R.id.rv_list)
                        val adapter = SheetChooseAdapter()
                        adapter.submitList(_sheets)
                        rv.layoutManager = LinearLayoutManager(this@MusicListActivity)
                        rv.adapter = adapter
                        adapter.addOnItemChildClickListener(R.id.btn_sheet,object:OnItemChildClickListener<Sheet>{
                            override fun invoke(
                                adapter: BaseQuickAdapter<Sheet, *>,
                                view: View,
                                position: Int
                            ) {
                                var taskCount = 0
                                val call = object:Call<Boolean>{
                                    override fun success(data: Boolean) {
                                        taskCount++
                                        if(taskCount == 4){
                                            toast("already collect")
                                        }
                                    }

                                    override fun error(e: Throwable) {
                                        toast(e.message.toString())
                                    }

                                }
                                val sheet = adapter.getItem(position)
                                val sheetInfo = SheetInfo(null,
                                    Global.user!!.UUID,sheet!!.token,media.rid.toString())
                                _localDBService.insertMedia(eMedia,call)
                                _localDBService.insert(sheetInfo,call)
                                _memfireDBService.insertMedia(eMedia,call)
                                _memfireDBService.insertSheetInfo(sheetInfo,call)
                                dismiss()
                            }

                        })
                    }
                }
                FLAG_UN_COLLECT -> {
                    var taskCount = 0
                    val call = object:Call<Boolean>{
                        override fun success(data: Boolean) {
                            taskCount++
                            if(taskCount == 2){
                                toast("already cancel collect")
                                _adapter.removeAt(position)
                            }
                        }
                        override fun error(e: Throwable) {
                            toast(e.message.toString())
                        }
                    }
                    _localDBService.deleteSheetInfo(media.rid,Global.user!!.UUID,call)
                    _memfireDBService.deleteSheetInfo(media.rid.toString(),Global.user!!.UUID,call)
                }
                FLAG_FAVORITE -> {
                    var taskCount = 0
                    val call = object:Call<Boolean>{
                        override fun success(data: Boolean) {
                            taskCount++
                            if(taskCount == 4){
                                toast("already favorite")
                            }
                        }
                        override fun error(e: Throwable) {
                            toast(e.message.toString())
                        }
                    }
                    val eFavoriteMedia = FavoriteMedia(null,media.rid, Global.user!!.UUID)
                    _localDBService.insertFavoriteMedia(eFavoriteMedia,call)
                    _localDBService.insertMedia(eMedia,call)
                    _memfireDBService.insertFavoriteMedia(eFavoriteMedia,call)
                    _memfireDBService.insertMedia(eMedia,call)
                }
                FLAG_UN_FAVORITE -> {
                    var taskCount = 0
                    val call = object:Call<Boolean>{
                        override fun success(data: Boolean) {
                            taskCount++
                            if(taskCount == 2){
                                toast("already cancel favorite")
                            }
                        }
                        override fun error(e: Throwable) {
                            toast(e.message.toString())
                        }
                    }
                    _localDBService.deleteFavoriteMedia(Global.user!!.UUID,media.rid.toString(),call)
                    _memfireDBService.deleteFavoriteMedia(Global.user!!.UUID,media.rid.toString(),call)
                    _adapter.remove(media)
                }
            }
            menu.dismiss()
        }
        return menu
    }


    /**
     * 构建榜单菜单
     * @param billboards BillboardList
     * @return PowerMenu
     */
    fun buildBillboardMenu(billboards:BillboardList):PowerMenu{
        val builder = PowerMenu.Builder(this)
        billboards.list.forEach {
            builder.addItem(PowerMenuItem(it.name))
        }
        val header = TextView(this)
        val padding = 15
        header.setPadding(padding,padding,padding,padding)
        header.text = billboards.name
        builder.setHeaderView(header)
        builder.setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
        builder.setMenuRadius(10f)
        builder.setMenuShadow(10f)
        builder.setBackgroundAlpha(0f)
        val menu = builder.build()
        menu.setOnMenuItemClickListener { position, item ->
            val title = billboards.list.get(position).name
            _binding.cpTitle.tvTitle.text = title
            _params.title = title
            _pagingInfo.page = 1
            _pagingInfo.finish = false
            _adapter.items = listOf()
            _adapter.notifyDataSetChanged()
            menu.dismiss()
        }
        return menu
    }


    /**
     * 构建歌手菜单
     * @return PowerMenu
     */
    fun buildArtistMenu():PowerMenu{
        val FLAG_FAVORITE = "favorite"
        val builder = PowerMenu.Builder(this)
        builder.addItem(PowerMenuItem(FLAG_FAVORITE))
        builder.setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
        builder.setMenuRadius(10f)
        builder.setMenuShadow(10f)
        builder.setBackgroundAlpha(0f)
        val menu = builder.build()
        menu.setOnMenuItemClickListener { position, item ->
            when(item.title){
                FLAG_FAVORITE -> {
                    var taskCount = 0
                    val call = object:Call<Boolean>{
                        override fun success(data: Boolean) {
                            taskCount++
                            if(taskCount == 2){
                                toast("already favorite")
                            }
                        }

                        override fun error(e: Throwable) {
                            toast(e.message.toString())
                        }
                    }
                    _localDBService.insertFavoriteArtist(_artist,call)
                    _memfireDBService.insertFavoriteArtist(Global.user!!.UUID,_artist,call)
                }
            }
            menu.dismiss()
        }
        return menu
    }


    /**
     * 切换RecyclerView状态
     * @param status [0:success,1:loading,2:error]
     * @param error
     */
    fun changeStatus(status:Int,error:String){
        when(status){
            0 -> {
                _adapter.emptyView?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = View.INVISIBLE
                _adapter.emptyView?.findViewById<ConstraintLayout>(R.id.cl_error)?.visibility = View.INVISIBLE
            }
            1 -> {
                _adapter.emptyView?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = View.VISIBLE
                _adapter.emptyView?.findViewById<ConstraintLayout>(R.id.cl_error)?.visibility = View.INVISIBLE
            }
            2 -> {
                _adapter.emptyView?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = View.INVISIBLE
                _adapter.emptyView?.findViewById<ConstraintLayout>(R.id.cl_error)?.visibility = View.VISIBLE
                _adapter.emptyView?.findViewById<TextView>(R.id.tv_error)?.text = error
            }
        }
    }


    /**
     * 加载数据
     */
    fun onLoadData(){
        when(_params.key){
            MStatus.SEARCH -> {
                loadSearchResult()
            }
            MStatus.BILLBOARD -> {
                loadBillboardMusicList()
            }
            MStatus.FAVORITE -> {
                loadFavoriteMusicList()
            }
            MStatus.SHEET -> {
                loadSheetMusicList()
            }
            MStatus.ARTIST -> {
                loadArtistMusicList()
            }
        }
    }


    /**
     * 加载搜索结果
     */
    fun loadSearchResult(){
        _pagingInfo.pageSize = 20
        _musicService.getSearchResult(_params.value,_pagingInfo.page,_pagingInfo.pageSize,object:Call<List<Media>>{
            override fun success(data: List<Media>) {
                if(_pagingInfo.page == 1)
                    _adapter.submitList(data)
                else
                    _adapter.addAll(data)

                _pagingInfo.page++
                _pagingInfo.finish = data.size != _pagingInfo.pageSize
                _pagingInfo.helper!!.trailingLoadState = LoadState.NotLoading(_pagingInfo.finish)
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
                changeStatus(2,e.message.toString())
            }

        })


    }


    /**
     * 加载歌手歌曲列表
     */
    fun loadArtistMusicList(){
        _pagingInfo.pageSize = 20
        _musicService.getArtistMusicList(_params.value,_pagingInfo.page,_pagingInfo.pageSize,object:Call<List<Media>>{
            override fun success(data: List<Media>) {
                if(_pagingInfo.page == 1)
                    _adapter.submitList(data)
                else
                    _adapter.addAll(data)

                _pagingInfo.page++
                _pagingInfo.finish = data.size != _pagingInfo.pageSize
                _pagingInfo.helper!!.trailingLoadState = LoadState.NotLoading(_pagingInfo.finish)
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
                changeStatus(2,e.message.toString())
            }
        })
    }


    /**
     * 加载歌手信息
     */
    fun loadArtistInfo(){
        _musicService.getArtistInfo(_params.value,object:Call<ArtistInfo>{
            override fun success(data: ArtistInfo) {
                _artist = data
                setTitle(_artist.name)
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }

        })
    }


    /**
     * 加载榜单音乐
     */
    fun loadBillboardMusicList(){
        val title = _params.title
        var billboard:Billboard? = null
        _billboards.list.forEach {
            if(it.name.equals(title)){
                billboard = it
                return@forEach
            }
        }
        if(billboard == null)
        {
            toast("billboard not found")
            return
        }

        _pagingInfo.pageSize = 20
        _musicService.getBillboardMusicList(billboard!!.sourceid,_pagingInfo.page,_pagingInfo.pageSize,object:Call<List<Media>>{
            override fun success(data: List<Media>) {
                if(_pagingInfo.page == 1){
                    _adapter.submitList(data)
                    _binding.rvList.scrollToPosition(0)
                }
                else
                    _adapter.addAll(data)

                _pagingInfo.page++
                _pagingInfo.finish = data.size != _pagingInfo.pageSize
                _pagingInfo.helper!!.trailingLoadState = LoadState.NotLoading(_pagingInfo.finish)
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
                changeStatus(2,e.message.toString())
            }
        })
    }

    /**
     * 加载喜爱音乐
     */
    fun loadFavoriteMusicList(){
        _pagingInfo.pageSize = 50
        _localDBService.queryFavoriteMedias(Global.user!!.UUID,_pagingInfo.page,_pagingInfo.pageSize,object:Call<List<Media>>{
            override fun success(data: List<Media>) {
                if(_pagingInfo.page == 1)
                    _adapter.submitList(data)
                else
                    _adapter.addAll(data)

                _pagingInfo.page++
                _pagingInfo.finish = data.size != _pagingInfo.pageSize
                _pagingInfo.helper!!.trailingLoadState = LoadState.NotLoading(_pagingInfo.finish)
                changeStatus(0,"")
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
                changeStatus(2,e.message.toString())
            }

        })
    }


    /**
     * 加载歌单歌曲
     */
    fun loadSheetMusicList(){
        _pagingInfo.pageSize = 50
        _localDBService.querySheetMedias(_sheet.token,_pagingInfo.page,_pagingInfo.pageSize,object:Call<List<Media>>{
            override fun success(data: List<Media>) {
                if(_pagingInfo.page == 1)
                    _adapter.submitList(data)
                else
                    _adapter.addAll(data)

                _pagingInfo.page++
                _pagingInfo.finish = data.size != _pagingInfo.pageSize
                _pagingInfo.helper!!.trailingLoadState = LoadState.NotLoading(_pagingInfo.finish)
                changeStatus(0,"")
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
                changeStatus(2,e.message.toString())
            }

        })
    }
}