package com.jqwong.music.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.OnItemChildClickListener
import com.chad.library.adapter.base.OnItemClickListener
import com.chad.library.adapter.base.OnItemLongClickListener
import com.google.gson.Gson
import com.jqwong.music.R
import com.jqwong.music.adapter.FavoriteArtistAdapter
import com.jqwong.music.adapter.SheetAdapter
import com.jqwong.music.app.Constant
import com.jqwong.music.app.Global
import com.jqwong.music.databinding.ActivityMainBinding
import com.jqwong.music.entity.FavoriteArtist
import com.jqwong.music.entity.Sheet
import com.jqwong.music.event.*
import com.jqwong.music.model.*
import com.jqwong.music.service.KuWoService
import com.jqwong.music.service.LocalDBService
import com.jqwong.music.service.MemfireDBService
import com.jqwong.music.utils.AudioPlayerUtil
import com.jqwong.music.utils.DateTimeUtil
import com.jqwong.music.utils.IDUtil
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class MainActivity:BaseActivity<ActivityMainBinding>(){

    private lateinit var _billboardMenu:PowerMenu
    private lateinit var _mainMenu:PowerMenu
    private lateinit var _sheetAdapter:SheetAdapter
    private val _localDBService = LocalDBService()
    private val _memfireDBService = MemfireDBService()

    override fun Title(): String {
        return "MINE"
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadBillboardList(buildBillboardMenu())
        loadSheets()
    }

    override fun initListener() {
        _binding.btnSearch.setOnClickListener {
            toSearchPage()
        }

        _binding.btnBillboard.setOnClickListener {
            if(_billboardMenu != null){
                _billboardMenu.showAsAnchorLeftBottom(_binding.btnBillboard)
            }
        }

        _binding.cpTitle.tvTitle.setOnLongClickListener {
            if(_mainMenu != null){
                _mainMenu.showAsAnchorLeftBottom(_binding.cpTitle.tvTitle)
            }
            true
        }

        _sheetAdapter.addOnItemChildClickListener(R.id.btn_sheet_detail,object:OnItemClickListener<Sheet>{
            override fun invoke(adapter: BaseQuickAdapter<Sheet, *>, view: View, position: Int) {
                val sheet = adapter.getItem(position)!!
                startActivity(Intent(this@MainActivity,MusicListActivity::class.java).putExtra(
                    Constant.ExtraKey,Gson().toJson(MusicCondition(sheet.name,MStatus.SHEET,Gson().toJson(sheet)))
                ))
            }
        })

        _sheetAdapter.addOnItemChildLongClickListener(R.id.btn_sheet_detail,object:OnItemLongClickListener<Sheet>{
            override fun invoke(
                adapter: BaseQuickAdapter<Sheet, *>,
                view: View,
                position: Int
            ): Boolean {
                buildSheetMenu(adapter.getItem(position)!!).showAsAnchorLeftBottom(view)
                return true
            }
        })

        _binding.btnFavorite.setOnClickListener {
            toMusicListPage(MusicCondition("FAVORITE",MStatus.FAVORITE,""))
        }

        _binding.cpPlayBar.btnPlay.setOnClickListener {
            AudioPlayerUtil.changeStatus()
        }

        _binding.clPlayBar.setOnClickListener {
            startActivity(Intent(this,LyricActivity::class.java))
        }

        _binding.btnSinger.setOnClickListener {
            showFavoriteArtistDialog()
        }
    }

    override fun initView() {
        _binding.tvUserName.text = Global.user!!.Name
        _mainMenu = buildMainMenu()
        _sheetAdapter = SheetAdapter()
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.HORIZONTAL
        _binding.rvSheet.layoutManager = llm
        _binding.rvSheet.adapter = _sheetAdapter
        PagerSnapHelper().attachToRecyclerView(_binding.rvSheet)
        if(Global.media != null){
            val media = Global.media!!
            val playerStatus = AudioPlayerUtil.getPlayerStatus()
            _binding.clPlayBar.visibility = View.VISIBLE
            _binding.cpPlayBar.tvSong.text = media.name + " - " + media.artist
            Glide.with(this)
                .asBitmap()
                .load(media.pic)
                .placeholder(R.color.black)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(_binding.cpPlayBar.ivPic)
            _binding.cpPlayBar.btnPlay.setImageResource(if(playerStatus == MediaStatus.PLAYING) R.drawable.ic_pause else R.drawable.ic_playon)
        }
    }

    override fun useEventBus(): Boolean {
        return true
    }


    /**
     * 获取榜单数据
     * @param call Call<List<BillboardList>>
     */
    fun loadBillboardList(call:Call<List<BillboardList>>){
        /**
         * from local database query
         * if exit data
         *    if date is today return
         *    else delete local data and api request
         * else api request
         *    then insert to local and return
         */
        val localEditCall = object:Call<Boolean>{
            override fun success(data: Boolean) {

            }

            override fun error(e: Throwable) {
                call.error(e)
            }
        }
        val apiQueryCall = object:Call<List<BillboardList>>{
            override fun success(data: List<BillboardList>) {
                _localDBService.deleteAll<com.jqwong.music.entity.Billboard>(localEditCall)
                _localDBService.insertBillBoards(data,localEditCall)
                call.success(data)
            }

            override fun error(e: Throwable) {
                call.error(e)
            }
        }
        val localQueryCall = object:Call<List<BillboardList>>{
            override fun success(data: List<BillboardList>) {
                if(data.size > 0){
                    run break1@{
                        data.forEach {
                            run break2@{
                                it.list.forEach {
                                    if(it.time == DateTimeUtil.getDate()){
                                        call.success(data)
                                    }
                                    else{
                                        _localDBService.deleteAll<com.jqwong.music.entity.Billboard>(localEditCall)
                                        KuWoService().getBillboardList(apiQueryCall)
                                    }
                                    return@break2
                                }
                            }
                            return@break1
                        }
                    }
                }
                else{
                    KuWoService().getBillboardList(apiQueryCall)
                }
            }

            override fun error(e: Throwable) {
                call.error(e)
            }
        }
        _localDBService.queryBillBoards(localQueryCall)
    }


    /**
     * 加载歌单
     */
    fun loadSheets(){
        val call = object:Call<List<Sheet>>  {
            override fun success(data: List<Sheet>) {
                _sheetAdapter.submitList(data)
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }
        }
        _localDBService.loadAll(call)
    }

    fun loadFavoriteArtist(call:Call<List<FavoriteArtist>>){
        _localDBService.loadAll(call)
    }


    /**
     * 构建榜单菜单
     * @return Call<List<BillboardList>>
     */
    fun buildBillboardMenu():Call<List<BillboardList>>{
        return object:Call<List<BillboardList>>{
            override fun success(data: List<BillboardList>) {
                val builder = PowerMenu.Builder(this@MainActivity)
                builder.setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                builder.setMenuRadius(10f)
                builder.setMenuShadow(10f)
                builder.setBackgroundAlpha(0f)
                data.forEach {
                    builder.addItem(PowerMenuItem(it.name))
                }
                val menu = builder.build()
                menu.setOnMenuItemClickListener { position, item ->
                    val billboards = data.get(position)
                    val params = MusicCondition("", MStatus.BILLBOARD, Gson().toJson(billboards))
                    startActivity(Intent(this@MainActivity,MusicListActivity::class.java).apply {
                        putExtra(Constant.ExtraKey, Gson().toJson(params))
                    })
                    menu.dismiss()
                }
                _billboardMenu = menu
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }

        }
    }


    /**
     * 构建主菜单
     */
    fun buildMainMenu():PowerMenu{
        val FLAG_LOGOUT = "Logout"
        val FLAG_ADD_SHEET = "add sheet"
        val builder = PowerMenu.Builder(this)
        builder.setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
        builder.setMenuRadius(10f)
        builder.setMenuShadow(10f)
        builder.setBackgroundAlpha(0f)
        builder.addItem(PowerMenuItem(FLAG_LOGOUT))
        builder.addItem(PowerMenuItem(FLAG_ADD_SHEET))
        val menu = builder.build()
        menu.setOnMenuItemClickListener { position, item ->
            when(item.title){
                FLAG_LOGOUT -> {
                    val sp = getSharedPreferences(Constant.USER, MODE_PRIVATE)
                    with(sp.edit()){
                        putString(Constant.USER, "")
                        apply()
                    }
                    Global.user = null
                    startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                    finish()
                }
                FLAG_ADD_SHEET -> {
                    showEditSheetDialog()
                }
            }
            menu.dismiss()
        }
        return menu
    }


    /**
     * 构建歌单菜单
     * @param sheet Sheet
     * @return PowerMenu
     */
    fun buildSheetMenu(sheet: Sheet):PowerMenu{
        val FLAG_UPDATE = "update"
        val FLAG_DELETE = "delete"
        val builder = PowerMenu.Builder(this)
        builder.addItem(PowerMenuItem(FLAG_UPDATE))
        builder.addItem(PowerMenuItem(FLAG_DELETE))
        builder.setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
        builder.setMenuRadius(10f)
        builder.setMenuShadow(10f)
        builder.setBackgroundAlpha(0f)
        val menu = builder.build()
        menu.setOnMenuItemClickListener { position, item ->
            when(item.title){
                FLAG_UPDATE -> {
                    showEditSheetDialog(sheet)
                }
                FLAG_DELETE -> {
                    MaterialDialog(this).show {
                        title(text = "Confirm")
                        message(text = "Sure to delete this sheet?")
                        positiveButton(R.string.submit){
                            editSheet(sheet,true)
                        }
                        negativeButton(R.string.cancel)
                    }
                }
            }
            menu.dismiss()
        }
        return menu
    }


    /**
     * 显示歌单编辑dialog
     * @param sheet Sheet?
     */
    fun showEditSheetDialog(sheet:Sheet? = null){
        var sheetName:String = ""
        if(sheet != null){
            sheetName = sheet.name
        }
        MaterialDialog(this)
            .title(text = "Edit Sheet")
            .show {
                input(
                    waitForPositiveButton = false,
                    prefill = sheetName
                ){
                    dialog,text ->
                        sheetName = text.toString()
                }
                negativeButton(R.string.submit){
                    if(sheet != null){
                        sheet.name = sheetName
                        editSheet(sheet)
                    }
                    else{
                        val newSheet = Sheet(null,sheetName, Global.user!!.UUID,IDUtil.newUUID())
                        editSheet(newSheet)
                    }
                }
                positiveButton(R.string.cancel)
        }
    }


    /**
     * 创建修改删除歌单
     * @param sheet Sheet?
     */
    fun editSheet(sheet: Sheet,delete:Boolean = false){
        var i = 0
        var taskCount = 0
        var flag = ""
        val editCall = object:Call<Boolean>{
            override fun success(data: Boolean) {
                i++
                if(i == taskCount){
                    toast("${flag} sheet success")
                    loadSheets()
                }
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }
        }

        if(delete){
            flag = "delete"
            taskCount = 4
            _localDBService.delete(sheet,editCall)
            _localDBService.deleteSheetInfoByToken(sheet.token,editCall)
            _memfireDBService.deleteSheet(sheet,editCall)
            _memfireDBService.deleteSheetInfo(sheet.token,editCall)
            return
        }

        if(sheet.id == null){
            /**
             * add sheet to local database and memfire database
             */
            taskCount = 2
            flag = "create"
            _localDBService.insert(sheet,editCall)
            _memfireDBService.insertSheet(sheet,editCall)
        }
        else{
            flag = "update"
            taskCount = 2
            _localDBService.update(sheet,editCall)
            _memfireDBService.updateSheet(sheet,editCall)
        }
    }


    /**
     * 显示喜爱歌手
     */
    fun showFavoriteArtistDialog(){
        MaterialDialog(this, BottomSheet()).show {
            title(R.string.artist)
            customView(R.layout.component_rv)
            cornerRadius(16f)
            val rv = view.contentLayout.findViewById<RecyclerView>(R.id.rv_list)
            val adapter = FavoriteArtistAdapter()
            loadFavoriteArtist(object:Call<List<FavoriteArtist>>{
                override fun success(data: List<FavoriteArtist>) {
                    val t = mutableListOf<ArtistInfo>()
                    data.forEach {
                        t.add(ArtistInfo(it.name,it.pic70,it.aId))
                    }
                    adapter.submitList(t)
                }

                override fun error(e: Throwable) {
                    toast(e.message.toString())
                }

            })
            rv.layoutManager = LinearLayoutManager(this@MainActivity)
            rv.adapter = adapter
            adapter.addOnItemChildClickListener(R.id.cl_item_artist,object:OnItemChildClickListener<ArtistInfo>{
                override fun invoke(
                    adapter: BaseQuickAdapter<ArtistInfo, *>,
                    view: View,
                    position: Int
                ) {
                    val item = adapter.getItem(position)
                    startActivity(Intent(this@MainActivity,MusicListActivity::class.java).putExtra(
                        Constant.ExtraKey,Gson().toJson(MusicCondition(item!!.name,MStatus.ARTIST,item.id.toString()))
                    ))
                    //dismiss()
                }
            })
            adapter.addOnItemChildLongClickListener(R.id.cl_item_artist,object:OnItemLongClickListener<ArtistInfo>{
                override fun invoke(
                    adapter: BaseQuickAdapter<ArtistInfo, *>,
                    view: View,
                    position: Int
                ): Boolean {
                    val artist = adapter.getItem(position)
                    val FLAG_UN_FAVORITE = "Un Favorite"
                    val builder = PowerMenu.Builder(this@MainActivity)
                    builder.addItem(PowerMenuItem(FLAG_UN_FAVORITE))
                    builder.setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
                    builder.setMenuRadius(10f)
                    builder.setMenuShadow(10f)
                    builder.setBackgroundAlpha(0f)
                    val menu = builder.build()
                    menu.setOnMenuItemClickListener { position, item ->
                        when(item.title){
                            FLAG_UN_FAVORITE -> {
                                var taskCount = 0
                                val call = object:Call<Boolean>{
                                    override fun success(data: Boolean) {
                                        taskCount++
                                        if(taskCount == 2){
                                            toast("already un favorite")
                                        }
                                    }

                                    override fun error(e: Throwable) {
                                        toast(e.message.toString())
                                    }
                                }
                                _localDBService.deleteFavoriteArtist(artist!!,call)
                                _memfireDBService.deleteFavoriteArtist(artist.id,Global.user!!.UUID,call)
                            }
                        }
                        menu.dismiss()
                        dismiss()
                    }
                    menu.showAsAnchorRightBottom(view)
                    return true
                }

            })

        }
    }


    /**
     * 音乐切换事件
     * @param event MediaChangeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event: MediaChangeEvent){
        if(_binding.clPlayBar.visibility == View.GONE){
            _binding.clPlayBar.visibility = View.VISIBLE
        }
        _binding.cpPlayBar.tvSong.text = event.media.name + " - " + event.media.artist
        Glide.with(this)
            .asBitmap()
            .load(event.media.pic)
            .placeholder(R.color.black)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(_binding.cpPlayBar.ivPic)
        _binding.cpPlayBar.pbSong.progress = 0
    }

    /**
     * 播放器状态改变事件
     * @param event PlayerStatusChangeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStatusChangeEvent(event:PlayerStatusChangeEvent){
        _binding.cpPlayBar.btnPlay.setImageResource(if(event.status == MediaStatus.PLAYING) R.drawable.ic_pause else R.drawable.ic_playon)
    }

    /**
     * 播放进度改变事件
     * @param event MediaPositionChangeEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPositionChangeEvent(event: MediaPositionChangeEvent){
        if(Global.media != null && Global.media?.time != "0".toLong()){
            val process = (event.position * 1.0 /Global.media!!.time * 100).toInt()
            _binding.cpPlayBar.pbSong.progress = process


            if(Global.lyric != null && Global.lyric?.data != null && Global.lyric?.data!!.lrclist != null){
                val _index = Global.lyric?.data!!.FindCurrentIndex(event.position)
                if(Global.lyricIndex != _index && _index >= 0 && _index < Global.lyric?.data!!.lrclist.size){
                    Global.lyricIndex = _index
                    EventBus.getDefault().post(LyricIndexChangeEvent(index = _index))
                    _binding.cpPlayBar.tvLyric.text = Global.lyric?.data!!.lrclist.get(_index).lineLyric
                }
            }
        }
    }

    /**
     * 歌词加载事件
     * @param event LyricLoadEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricLoadEvent(event: LyricLoadEvent){
        var tip = ""
        when(event.status){
            LyricLoadStatus.LOADING -> {
                tip = "loading lyric.."
            }
            LyricLoadStatus.ERROR -> {
                tip = "sorry, load lyric failed"
            }
            LyricLoadStatus.SUCCESS -> {
                if(event.lyric != null && event.lyric.data.lrclist.isNotEmpty() ){
                    tip = event.lyric.data.lrclist.first().lineLyric
                }
            }
        }
        _binding.cpPlayBar.tvLyric.text = tip
    }

    fun toSearchPage(){
        startActivity(Intent(this@MainActivity,SearchActivity::class.java))
    }

    fun toMusicListPage(params:MusicCondition){
        startActivity(Intent(this@MainActivity,MusicListActivity::class.java)
            .putExtra(Constant.ExtraKey,Gson().toJson(params)))
    }
}