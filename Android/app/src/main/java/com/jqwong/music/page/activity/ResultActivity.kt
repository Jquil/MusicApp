package com.jqwong.music.page.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.jqwong.music.R
import com.jqwong.music.adapter.SongAdapter
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityResultBinding
import com.jqwong.music.model.*
import com.jqwong.music.page.listener.DoubleClickListener
import com.jqwong.music.service.CollectService
import com.jqwong.music.service.SearchService
import com.jqwong.music.utils.CommonUtil
import com.jqwong.music.utils.audio.AudioPlayerUtil

class ResultActivity : BaseActivity() {

    private lateinit var _Binding : ActivityResultBinding

    private var _SearchService:SearchService

    private var _CollectService:CollectService

    private lateinit var _SAdapter:SongAdapter

    private var _LoadInfo:LoadInfo = LoadInfo()

    private lateinit var _ResultCondition:ResultCondition


    private lateinit var _CurrentArtist:ArtistInfo


    init{
        _SearchService = SearchService()
        _CollectService = CollectService()
    }



    override fun InitData(){
        when(_ResultCondition.key)
        {
            ResultKey.Search -> {
                LoadSearchResult(_ResultCondition.data)
            }
            ResultKey.Favorite -> {
                LoadFavoriteList(GlobalObject.User!!.id)
            }
            ResultKey.Sheet -> {
                LoadSheetMediaList(GlobalObject.User!!.id,_ResultCondition.data)
            }
            ResultKey.Artist -> {
                LoadArtistInfo(_ResultCondition.data)
                LoadArtistSong(_ResultCondition.data)
            }
            ResultKey.Bang -> {
                LoadBangMenu{
                    _Binding.cpTitle.tvTitle.text = it.name
                    _ResultCondition.data = it.sourceid
                    LoadBangSong(it.sourceid)
                }
            }
        }
    }



    override fun InitView(){
        _Binding.cpTitle.tvTitle.text = _ResultCondition.title
        _Binding.rvList.layoutManager = LinearLayoutManager(this)



        _SAdapter = SongAdapter()
        _SAdapter.setEmptyViewLayout(this, R.layout.component_status)
        _SAdapter.isEmptyViewEnable = true
        _LoadInfo.helper = QuickAdapterHelper.Builder(_SAdapter)
            .setTrailingLoadStateAdapter(object: TrailingLoadStateAdapter.OnTrailingListener{
                override fun onFailRetry() {

                }

                override fun onLoad() {
                    when(_ResultCondition.key){
                        ResultKey.Search    -> LoadSearchResult(_ResultCondition.data)
                        ResultKey.Favorite  -> LoadFavoriteList(GlobalObject.User!!.id)
                        ResultKey.Sheet     -> LoadSheetMediaList(GlobalObject.User!!.id,_ResultCondition.data)
                        ResultKey.Artist    -> LoadArtistSong(_ResultCondition.data)
                        ResultKey.Bang      -> LoadBangSong(_ResultCondition.data)
                    }
                }
            }).build()
        _Binding.rvList.adapter = _LoadInfo.helper!!.adapter
    }



    override fun InitListener(){
        _Binding.cpReturn.btnReturn.setOnClickListener {
            this.finish()
        }

        _Binding.swlList.setOnRefreshListener {
            _LoadInfo.page = 1
            _LoadInfo.finish = false
            ChangeStatus(0)

            when(_ResultCondition.key){
                ResultKey.Search -> {
                    LoadSearchResult(_ResultCondition.data)
                }
                ResultKey.Favorite -> {
                    LoadFavoriteList(GlobalObject.User!!.id)
                }
                ResultKey.Sheet -> {
                    LoadSheetMediaList(GlobalObject.User!!.id,_ResultCondition.data)
                }
                ResultKey.Artist -> {
                    LoadArtistSong(_ResultCondition.data)
                }
                ResultKey.Bang -> {
                    LoadBangSong(_ResultCondition.data)
                }
            }

            _SAdapter.notifyDataSetChanged()
            _Binding.swlList.isRefreshing = false
        }

        _SAdapter.setOnItemClickListener { adapter, view, position ->
            val list = adapter.items.slice(position..adapter.items.size-1)
            val medias = mutableListOf<Media>()
            list.forEachIndexed { index, song ->
                var media = Media("",song.album,song.artist,song.name,song.rid.toString(),if(song.pic != null) song.pic else if (song.pic120 != null) song.pic120 else "https://jqwong.cn/static/img/avatar1.aefad32.png",CommonUtil.StrToTime(song.songTimeMinutes),song.songTimeMinutes,song.artistid)
                medias.add(media)
            }
            AudioPlayerUtil.PrepareAndPlay(medias)
        }

        _SAdapter.setOnItemLongClickListener { adapter, view, position ->
            val item = adapter.getItem(position)
            ShowMediaToolDialog(item!!)
            true
        }

        _Binding.cpTitle.tvTitle.setOnLongClickListener {
            startActivity(Intent(this,LyricActivity::class.java))
            true
        }


        if(_ResultCondition.key == ResultKey.Artist){
            _Binding.cpTitle.tvTitle.setOnClickListener(object: DoubleClickListener(){
                override fun onDoubleClick(v: View?) {
                    if(_CurrentArtist == null)
                        return
                    _CollectService.Artist(GlobalObject.User!!.id, _CurrentArtist)
                    Toast.makeText(this@ResultActivity,"liked",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }



    override fun SetContentView(savedInstanceState: Bundle?) {
        _Binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(_Binding.root)
        val objStr = intent.getStringExtra(GlobalObject.ExtraKey)
        if(objStr == null || objStr == ""){
            Toast.makeText(this,"${TAG} about extra-key not found",Toast.LENGTH_SHORT).show()
            this.finish()
        }
        _ResultCondition = Gson().fromJson(objStr,ResultCondition::class.java)
    }



    override fun Destory() {
    }


    fun LoadSearchResult(key:String){
        _SearchService.GetSearchResult(key,_LoadInfo.page,_LoadInfo.size,object: HttpCall<List<Song>>{
            override fun onError(e: Throwable) {
                if(_SAdapter.items.size == 0){
                    ChangeStatus(1, error = e.message.toString())
                }
                Toast.makeText(this@ResultActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun OnSuccess(t: List<Song>) {
                if(_LoadInfo.page == 1)
                    _SAdapter.submitList(t)
                else
                    _SAdapter.addAll(t)

                _LoadInfo.page++
                _LoadInfo.finish = t.size != _LoadInfo.size
                _LoadInfo.helper!!.trailingLoadState = LoadState.NotLoading(_LoadInfo.finish)
            }
        })
    }


    fun LoadFavoriteList(userId:Long){
        val list = _CollectService.LoadFavoriteList(userId,_LoadInfo.page,_LoadInfo.size)
        val data = mutableListOf<Song>()
        list.forEach {
            data.add(Song(it.rid,it.rid.toInt(),it.artist,it.pic,it.album,0,it.name,it.pic,it.strTime,it.time,it.Url,it.artistId))
        }

        if(_LoadInfo.page == 1){
            _SAdapter.submitList(data)
            if(data.size == 0)
                ChangeStatus(1,App.Tip.LoadFavoriteEmpty)
        }
        else
            _SAdapter.addAll(data)


        _LoadInfo.page++
        _LoadInfo.finish = data.size != _LoadInfo.size
        _LoadInfo.helper!!.trailingLoadState = LoadState.NotLoading(_LoadInfo.finish)
    }


    fun LoadSheetMediaList(userId: Long, sheetToken:String){
        val list = _CollectService.LoadSheetMediaList(userId,sheetToken,_LoadInfo.page,_LoadInfo.size)
        val data = mutableListOf<Song>()
        list.forEach {
            data.add(Song(it.rid,it.rid.toInt(),it.artist,it.pic,it.album,0,it.name,it.pic,it.strTime,it.time,it.Url,it.ArtistId))
        }
        if(_LoadInfo.page == 1){
            _SAdapter.submitList(data)
            if(data.size == 0)
                ChangeStatus(1,App.Tip.LoadSheetMediaEmpty)
        }
        else
            _SAdapter.addAll(data)


        _LoadInfo.page++
        _LoadInfo.finish = data.size != _LoadInfo.size
        _LoadInfo.helper!!.trailingLoadState = LoadState.NotLoading(_LoadInfo.finish)
    }


    fun LoadArtistInfo(artistId:String){
        _SearchService.GetArtistInfo(artistId,object:HttpCall<ArtistInfo>{
            override fun onError(e: Throwable) {
                Toast.makeText(this@ResultActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun OnSuccess(t: ArtistInfo) {
                _CurrentArtist = t
                _Binding.cpTitle.tvTitle.text = t.name
            }
        })
    }


    fun LoadArtistSong(artistId: String){
        _SearchService.GetArtistSong(artistId,_LoadInfo.page,_LoadInfo.size,object: HttpCall<List<Song>>{
            override fun onError(e: Throwable) {
                if(_SAdapter.items.size == 0){
                    ChangeStatus(1, error = e.message.toString())
                }
                Toast.makeText(this@ResultActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun OnSuccess(t: List<Song>) {
                if(_LoadInfo.page == 1)
                    _SAdapter.submitList(t)
                else
                    _SAdapter.addAll(t)

                _LoadInfo.page++
                _LoadInfo.finish = t.size != _LoadInfo.size
                _LoadInfo.helper!!.trailingLoadState = LoadState.NotLoading(_LoadInfo.finish)
            }
        })
    }


    fun LoadBangMenu(call:(bangItem: BangItem) -> Unit ){
        _SearchService.GetBangMenu(object:HttpCall<List<Bang>>{
            override fun onError(e: Throwable) {
                ChangeStatus(1,App.Tip.LoadBangError)
            }

            override fun OnSuccess(t: List<Bang>) {
                t.forEach {
                    it.list.forEach {
                        it.name = it.name.replace("酷我","")
                        if(it.name.contains("热歌榜")){
                            call.invoke(it)
                            return
                        }
                    }
                    call.invoke(it.list.first())
                    return
                }
            }
        })
    }


    fun LoadBangSong(sourceId:String){
        _SearchService.GetBangSong(sourceId,_LoadInfo.page,_LoadInfo.size,object: HttpCall<List<Song>>{
            override fun onError(e: Throwable) {
                if(_SAdapter.items.size == 0){
                    ChangeStatus(1, error = e.message.toString())
                }
                Toast.makeText(this@ResultActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun OnSuccess(t: List<Song>) {
                if(_LoadInfo.page == 1)
                    _SAdapter.submitList(t)
                else
                    _SAdapter.addAll(t)

                _LoadInfo.page++
                _LoadInfo.finish = t.size != _LoadInfo.size
                _LoadInfo.helper!!.trailingLoadState = LoadState.NotLoading(_LoadInfo.finish)
            }
        })
    }


    // 0:loading / 1:error
    fun ChangeStatus(status:Int,error:String = ""){
        when(status){
            0 -> {
                _SAdapter.emptyView?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = View.VISIBLE
                _SAdapter.emptyView?.findViewById<ConstraintLayout>(R.id.cl_error)?.visibility = View.INVISIBLE
            }

            1 -> {
                _SAdapter.emptyView?.findViewById<ProgressBar>(R.id.pb_loading)?.visibility = View.INVISIBLE
                _SAdapter.emptyView?.findViewById<ConstraintLayout>(R.id.cl_error)?.visibility = View.VISIBLE
                _SAdapter.emptyView?.findViewById<TextView>(R.id.tv_error)?.text = error
            }
        }
    }



    fun ShowMediaToolDialog(song: Song){
        when(_ResultCondition.key){
            ResultKey.Sheet -> {
                val dialog = BottomSheetDialog(this)
                val view:View = layoutInflater.inflate(R.layout.dialog_media_tool,null)
                dialog.setOnShowListener {
                    (view.parent as ViewGroup).background =
                        ColorDrawable(Color.TRANSPARENT)
                }
                view.findViewById<Button>(R.id.btn_remove).setOnClickListener {
                    _CollectService.DeleteOfSheetMedia(GlobalObject.User!!.id,_ResultCondition.data,song.rid)
                    _SAdapter.remove(song)
                    if(_SAdapter.items.size == 0){
                        ChangeStatus(1,App.Tip.LoadSheetMediaEmpty)
                    }
                    dialog.dismiss()
                }
                dialog.setContentView(view)
                dialog.show()
            }
            ResultKey.Favorite -> {
                val dialog = BottomSheetDialog(this)
                val view:View = layoutInflater.inflate(R.layout.dialog_media_tool,null)
                dialog.setOnShowListener {
                    (view.parent as ViewGroup).background =
                        ColorDrawable(Color.TRANSPARENT)
                }
                view.findViewById<Button>(R.id.btn_remove).setOnClickListener {
                    _CollectService.DeleteOfFavorite(GlobalObject.User!!.id,song.rid)
                    _SAdapter.remove(song)
                    if(_SAdapter.items.size == 0){
                        ChangeStatus(1,App.Tip.LoadFavoriteEmpty)
                    }
                    dialog.dismiss()
                }
                dialog.setContentView(view)
                dialog.show()
            }
            ResultKey.Search -> {

            }
            ResultKey.Artist -> {}
            ResultKey.Bang -> {}
        }
    }
}