package com.jqwong.music.page.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout.inflate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.OnItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.jqwong.music.R
import com.jqwong.music.adapter.ArtistAdapter
import com.jqwong.music.adapter.SheetAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityMainBinding
import com.jqwong.music.model.GlobalObject
import com.jqwong.music.model.ResultCondition
import com.jqwong.music.model.ResultKey
import com.jqwong.music.repository.entity.Sheet
import com.jqwong.music.service.CollectService
import com.jqwong.music.service.SheetService
import com.jqwong.music.utils.audio.AudioPlayerUtil
import com.jqwong.music.utils.eventbus.event.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity() {

    private lateinit var _Binding : ActivityMainBinding

    private var _SheetService:SheetService

    private lateinit var _SheetAdapter:SheetAdapter

    init {
        _SheetService = SheetService()
    }


    override fun InitView(){

        _Binding.cpTitle.tvTitle.text = "Mine"


        _SheetAdapter = SheetAdapter()
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.HORIZONTAL
        _Binding.rvSheet.layoutManager = llm
        _Binding.rvSheet.adapter = _SheetAdapter
        PagerSnapHelper().attachToRecyclerView(_Binding.rvSheet)

        if(GlobalObject.CurrentMedia != null){
            _Binding.clPlayBar.visibility = View.VISIBLE
            _Binding.cpPlayBar.tvSong.text = GlobalObject.CurrentMedia?.name + " - " + GlobalObject.CurrentMedia?.artist
            Glide.with(this)
                .asBitmap()
                .load(GlobalObject.CurrentMedia?.pic)
                .placeholder(R.color.black)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(_Binding.cpPlayBar.ivPic)
            _Binding.cpPlayBar.btnPlay.setImageResource(if(AudioPlayerUtil.GetState() == MediaState.Play) R.drawable.ic_pause else R.drawable.ic_playon)
            if(GlobalObject.Lyric == null){
                _Binding.cpPlayBar.tvLyric.text = App.Tip.LoadLyricError
            }
        }
    }


    override fun InitData(){
        LoadSheetList()
    }



    override fun InitListener() {
        _Binding.btnSearch.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }
        _Binding.clPlayBar.setOnLongClickListener {
            ShowGlobalToolDialog()
            true
        }

        _SheetAdapter.addOnItemChildLongClickListener(R.id.btn_sheet_detail) { adapter, view, position ->
            ShowSheetToolDialog(adapter.items.get(position))
            true
        }

        _SheetAdapter.addOnItemChildClickListener(R.id.btn_sheet_detail,object:OnItemClickListener<Sheet>{
            override fun invoke(adapter: BaseQuickAdapter<Sheet, *>, view: View, position: Int) {
                startActivity(Intent(this@MainActivity,ResultActivity::class.java).let {
                    val item = adapter.getItem(position)
                    val data = ResultCondition(key = ResultKey.Sheet, title = item?.name!!, data = item.token)
                    it.putExtra(GlobalObject.ExtraKey,Gson().toJson(data))
                })
            }
        })


        _Binding.clPlayBar.setOnClickListener {
            startActivity(Intent(this,LyricActivity::class.java))
        }


        _Binding.cpPlayBar.btnPlay.setOnClickListener {
            AudioPlayerUtil.PlayOrPause()
        }

        _Binding.btnFavorite.setOnClickListener {
            val req = ResultCondition(ResultKey.Favorite,_Binding.btnFavorite.text.toString(),"")
            startActivity(Intent(this,ResultActivity::class.java).apply {
                putExtra(GlobalObject.ExtraKey, Gson().toJson(req))
            })
        }


        _Binding.btnSinger.setOnClickListener {
            ShowArtistDialog()
            //Toast.makeText(this@MainActivity,"dadada",Toast.LENGTH_SHORT).show()
        }

        _Binding.btnBillboard.setOnClickListener {
            val req = ResultCondition(ResultKey.Bang,"","")
            startActivity(Intent(this@MainActivity,ResultActivity::class.java).apply {
                putExtra(GlobalObject.ExtraKey,Gson().toJson(req,ResultCondition::class.java))
            })
        }
    }



    override fun SetContentView(savedInstanceState: Bundle?) {
        _Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_Binding.root)
        EventBus.getDefault().register(this)
    }


    override fun Destory() {
        EventBus.getDefault().unregister(this)
    }


    private fun ShowGlobalToolDialog(){
        val sheet = BottomSheetDialog(this)
        val view:View = layoutInflater.inflate(R.layout.dialog_main_tool,null)
        sheet.setOnShowListener {
            (view.parent as ViewGroup).background =
                ColorDrawable(Color.TRANSPARENT)
        }
        sheet.setContentView(view)

        sheet.findViewById<Button>(R.id.btn_addSheet)?.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Sheet name")
            val view = inflate(this,R.layout.component_sheetname,null)
            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.btn_success).setOnClickListener {
                var sheetName = view.findViewById<EditText>(R.id.et_sheetName).text.toString()
                if(sheetName != ""){
                    _SheetService.Add(sheetName,GlobalObject.User!!.id)
                    _SheetAdapter.items = listOf()
                    _SheetAdapter.notifyDataSetChanged()
                    LoadSheetList()
                }
                dialog.dismiss()
            }

            dialog.show()
            sheet.dismiss()
        }

        sheet.show()
    }


    private fun ShowArtistDialog(){
        val service = CollectService()
        val data = service.LoadArtistList(GlobalObject.User!!.id)
        if(data.size == 0)
            return
        val dialog = BottomSheetDialog(this)
        val view:View = layoutInflater.inflate(R.layout.dialog_artist,null)
        dialog.setOnShowListener {
            (view.parent as ViewGroup).background =
                ColorDrawable(Color.TRANSPARENT)
        }
        val adapter = ArtistAdapter()
        adapter.submitList(data)
        val rv = view.findViewById<RecyclerView>(R.id.rv_list)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(this@MainActivity,ResultActivity::class.java).apply {
                putExtra(GlobalObject.ExtraKey,Gson().toJson(ResultCondition(ResultKey.Artist,"",adapter.getItem(position)?.id.toString()),ResultCondition::class.java))
            })
            dialog.dismiss()
        }
        adapter.setOnItemLongClickListener { adapter, view, position ->
            val service = CollectService()
            service.DeleteOfArtist(GlobalObject.User!!.id,adapter.getItem(position)?.id!!)
            adapter.removeAt(position)
            if(adapter.items.size == 0){
                dialog.dismiss()
            }
            true
        }
        dialog.setContentView(view)
        dialog.show()
    }



    private fun LoadSheetList(){
        val sheets = _SheetService.Load(GlobalObject.User!!.id)
        _SheetAdapter.submitList(sheets)
    }


    private fun ShowSheetToolDialog(sheet:Sheet){
        val btmDialog = BottomSheetDialog(this)
        val view:View = layoutInflater.inflate(R.layout.dialog_sheet_tool,null)
        btmDialog.setOnShowListener {
            (view.parent as ViewGroup).background =
                ColorDrawable(Color.TRANSPARENT)
        }

        view.findViewById<Button>(R.id.btn_rename).setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Sheet name")
            val view = inflate(this,R.layout.component_sheetname,null)
            builder.setView(view)
            val dialog = builder.create()
            val et = view.findViewById<EditText>(R.id.et_sheetName)
            et.setText(sheet.name)
            view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.btn_success).setOnClickListener {
                var sheetName = et.text.toString()
                if(sheetName != ""){
                    sheet.name = sheetName
                    _SheetService.Update(sheet)
                    _SheetAdapter.items = listOf()
                    _SheetAdapter.notifyDataSetChanged()
                    LoadSheetList()
                }
                dialog.dismiss()
            }

            dialog.show()
            btmDialog.dismiss()
        }

        view.findViewById<Button>(R.id.btn_delete).setOnClickListener {
            val service = CollectService()
            service.DeleteAllOfSheetMedia(GlobalObject.User!!.id,sheet.token)
            _SheetService.Delete(sheet)
            _SheetAdapter.items = listOf()
            _SheetAdapter.notifyDataSetChanged()
            LoadSheetList()
            btmDialog.dismiss()
        }
        btmDialog.setContentView(view)

        btmDialog.show()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMediaPositionChangeEvent(event:MediaPositionChangeEvent){
        if(GlobalObject.CurrentMedia != null && GlobalObject.CurrentMedia?.time != "0".toLong()){
            val process = (event.position * 1.0 /GlobalObject.CurrentMedia!!.time * 100).toInt()
            _Binding.cpPlayBar.pbSong.progress = process


            if(GlobalObject.Lyric != null && GlobalObject.Lyric?.data != null && GlobalObject.Lyric?.data!!.lrclist != null){
                val _index = GlobalObject.Lyric?.data!!.FindCurrentIndex(event.position)
                if(GlobalObject.CurrentLyricIndex != _index && _index >= 0 && _index < GlobalObject.Lyric?.data!!.lrclist.size){
                    GlobalObject.CurrentLyricIndex = _index
                    EventBus.getDefault().post(LyricIndexChangeEvent(index = _index))
                    _Binding.cpPlayBar.tvLyric.text = GlobalObject.Lyric?.data!!.lrclist.get(_index).lineLyric
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMediaChangeEvent(event:MediaChangeEvent){
        if(_Binding.clPlayBar.visibility == View.GONE){
            _Binding.clPlayBar.visibility = View.VISIBLE
        }
        _Binding.cpPlayBar.tvSong.text = event.media.name + " - " + event.media.artist
        Glide.with(this)
            .asBitmap()
            .load(event.media.pic)
            .placeholder(R.color.black)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(_Binding.cpPlayBar.ivPic)
        _Binding.cpPlayBar.pbSong.progress = 0
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnLyricIndexChangeEvent(event:LyricIndexChangeEvent){
        if(GlobalObject.Lyric == null || GlobalObject.Lyric?.data == null || GlobalObject.Lyric!!.data.lrclist == null)
        {
            //_Binding.cpPlayBar.tvLyric.text = App.Tip.LoadLyricError
            return
        }
        _Binding.cpPlayBar.tvLyric.text = GlobalObject.Lyric!!.data.lrclist[event.index].lineLyric
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnLyricLoadingEvent(event:LyricLoadingEvent){
        _Binding.cpPlayBar.tvLyric.text = App.Tip.LoadingLyric
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnLyricChangeEvent(event: LyricChangeEvent){
        if(event.lyric == null){
            _Binding.cpPlayBar.tvLyric.text = App.Tip.LoadLyricError
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMediaStateChangeEvent(event:MediaStateChangeEvent){
        _Binding.cpPlayBar.btnPlay.setImageResource(if(event.state == MediaState.Play) R.drawable.ic_pause else R.drawable.ic_playon)
    }
}