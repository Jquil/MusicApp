package com.jqwong.music.page.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Telephony.Sms.Intents
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.OnItemChildClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.jqwong.music.R
import com.jqwong.music.adapter.LyricAdapter
import com.jqwong.music.adapter.SelectSheetAdapter
import com.jqwong.music.adapter.lm.CenterLayoutManager
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.databinding.ActivityLyricBinding
import com.jqwong.music.model.GlobalObject
import com.jqwong.music.model.Lyric
import com.jqwong.music.model.ResultCondition
import com.jqwong.music.model.ResultKey
import com.jqwong.music.repository.entity.Sheet
import com.jqwong.music.service.CollectService
import com.jqwong.music.service.SearchService
import com.jqwong.music.service.SheetService
import com.jqwong.music.utils.audio.AudioPlayerUtil
import com.jqwong.music.utils.eventbus.event.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LyricActivity : BaseActivity() {

    private val _CollectService = CollectService()

    private lateinit var _Binding:ActivityLyricBinding

    private lateinit var _Adapter:LyricAdapter

    private var _AutoScroll = true

    private var _IsTouchOfSeek = false


    override fun InitView(){
        _Adapter = LyricAdapter()
        val manager = CenterLayoutManager(this)
        manager.orientation = (RecyclerView.VERTICAL)
        _Binding.rvList.layoutManager = manager
        _Binding.rvList.adapter = _Adapter
        if(GlobalObject.Lyric?.data?.lrclist != null){
            _Adapter.addAll(GlobalObject.Lyric!!.data.lrclist)
            _Adapter.notifyDataSetChanged()
            manager.scrollToPosition(GlobalObject.CurrentLyricIndex)
        }
        else{
            _Binding.tvLyricNotFound.visibility = View.VISIBLE
        }


        if(GlobalObject.CurrentMedia?.name != null){
            _Binding.cpTitle.tvTitle.text = GlobalObject.CurrentMedia?.name
        }

        _Binding.btnPlay.setImageResource(if(AudioPlayerUtil.GetState() == MediaState.Play) R.drawable.ic_pause else R.drawable.ic_playon)
    }


    override fun InitData(){

    }


    override fun InitListener(){
        _Binding.cpReturn.btnReturn.setOnClickListener {
            this.finish()
        }


        _Binding.rvList.setOnTouchListener { view, motionEvent ->
            true
        }

        _Binding.tvLyricNotFound.setOnClickListener {
            // reload
            if(GlobalObject.CurrentMedia?.rid != null){
                it.visibility = View.INVISIBLE
                _Binding.pbLoading.visibility = View.VISIBLE
                val service = SearchService()
                service.GetLyric(GlobalObject.CurrentMedia?.rid.toString().toInt(),object: HttpCall<Lyric> {
                    override fun onError(e: Throwable) {
                        it.visibility = View.VISIBLE
                        _Binding.pbLoading.visibility = View.INVISIBLE
                    }

                    override fun OnSuccess(t: Lyric) {
                        t.data.lrclist.forEach {
                            it.inTime = (it.time.toFloat() * 1000).toLong()
                        }
                        GlobalObject.CurrentLyricIndex = 0
                        GlobalObject.Lyric = t
                        EventBus.getDefault().post(LyricChangeEvent(t))
                    }
                })
            }
        }

        _Binding.btnNext.setOnClickListener {
            // next
            AudioPlayerUtil.Next()
        }

        _Binding.btnPre.setOnClickListener {
            // pre
            AudioPlayerUtil.Pre()
        }

        _Binding.btnPlay.setOnClickListener {
            // play and stop
            AudioPlayerUtil.PlayOrPause()
        }

        _Binding.sbPosition.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                _IsTouchOfSeek = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val position:Long = (GlobalObject.CurrentMedia!!.time * (p0?.progress!! / 100.0)).toLong()
                AudioPlayerUtil.SeekToPosition(position)
                _IsTouchOfSeek = false
                if(GlobalObject.Lyric?.data != null && GlobalObject.Lyric?.data!!.lrclist != null){
                    val index = GlobalObject.Lyric?.data!!.FindCurrentIndex(position)
                    _Binding.rvList.layoutManager.let {
                        it?.scrollToPosition(index)
                    }
                }
            }
        })


        _Binding.btnPlay.setOnLongClickListener {
            val dialog = BottomSheetDialog(this)
            val view:View = layoutInflater.inflate(R.layout.dialog_lyric_tool,null)
            dialog.setOnShowListener {
                (view.parent as ViewGroup).background =
                    ColorDrawable(Color.TRANSPARENT)
            }

            // change play mode
            val btnMode = view.findViewById<Button>(R.id.btn_playMode)
            btnMode.text = if(AudioPlayerUtil.IsRepeatMode() == 1) "ORDER" else "REPEAT"
            btnMode.setOnClickListener {
                if(GlobalObject.CurrentMedia == null)
                    return@setOnClickListener
                val mode = if (AudioPlayerUtil.IsRepeatMode()== 1 ) 0 else 1
                AudioPlayerUtil.SetRepeatMode(mode)
                btnMode.text = if(mode == 1) "ORDER" else "REPEAT"
            }

            // favorite
            view.findViewById<Button>(R.id.btn_favorite).setOnClickListener {
                if(GlobalObject.CurrentMedia == null)
                    return@setOnClickListener
                _CollectService.Favorite(GlobalObject.User!!.id,GlobalObject.CurrentMedia!!)
                dialog.dismiss()
                Toast.makeText(this@LyricActivity,"Already love",Toast.LENGTH_SHORT).show()
            }

            // Collect to sheet
            view.findViewById<Button>(R.id.btn_CollectToSheet).setOnClickListener {
                val service = SheetService()
                val sheets = service.Load(GlobalObject.User!!.id)
                if(sheets.size == 0){
                    Toast.makeText(this@LyricActivity,"Sorry, you didn't create sheet",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val dialogOfSelectSheet = BottomSheetDialog(this)
                val viewOfSelectSheet:View = layoutInflater.inflate(R.layout.dialog_select_sheet,null)
                dialogOfSelectSheet.setOnShowListener {
                    (viewOfSelectSheet.parent as ViewGroup).background =
                        ColorDrawable(Color.TRANSPARENT)
                }
                val rv = viewOfSelectSheet.findViewById<RecyclerView>(R.id.rv_list)
                val adapter = SelectSheetAdapter()
                rv.layoutManager = LinearLayoutManager(this)
                rv.adapter = adapter
                adapter.addOnItemChildClickListener(R.id.btn_sheet,object:OnItemChildClickListener<Sheet>{
                    override fun invoke(
                        adapter: BaseQuickAdapter<Sheet, *>,
                        view: View,
                        position: Int
                    ) {
                        val item = adapter.items.get(position)
                        val service = CollectService()
                        service.Collect(GlobalObject.User!!.id,item,GlobalObject.CurrentMedia!!)
                        dialogOfSelectSheet.dismiss()
                        Toast.makeText(this@LyricActivity,"Collect success",Toast.LENGTH_SHORT).show()
                    }
                })

                adapter.submitList(sheets)
                adapter.notifyDataSetChanged()
                dialogOfSelectSheet.setContentView(viewOfSelectSheet)
                dialogOfSelectSheet.show()
                dialog.dismiss()
            }

            // Query artist
            view.findViewById<Button>(R.id.btn_artistInfo).setOnClickListener {
                if(GlobalObject.CurrentMedia == null)
                    return@setOnClickListener
                startActivity(Intent(this@LyricActivity,ResultActivity::class.java).apply {
                    putExtra(GlobalObject.ExtraKey, Gson().toJson(ResultCondition(ResultKey.Artist,"",GlobalObject.CurrentMedia?.artistid.toString())))
                })
                dialog.dismiss()
            }
            dialog.setContentView(view)
            dialog.show()
            true
        }
    }

    override fun SetContentView(savedInstanceState: Bundle?) {
        _Binding = ActivityLyricBinding.inflate(layoutInflater)
        setContentView(_Binding.root)
        EventBus.getDefault().register(this)
    }

    override fun Destory() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnLyricIndexChangeEvent(event: LyricIndexChangeEvent){
        if(GlobalObject.Lyric?.data == null || GlobalObject.Lyric!!.data.lrclist == null){
            return
        }
        if(_AutoScroll){
            _Binding.rvList.layoutManager?.let {
                it.smoothScrollToPosition(_Binding.rvList,RecyclerView.State(),event.index)
            }
        }
        _Adapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnLyricChangeEvent(event: LyricChangeEvent){
        if(event.lyric == null || event.lyric.data == null || event.lyric.data.lrclist == null)
        {
            _Binding.tvLyricNotFound.visibility = View.VISIBLE
            _Binding.pbLoading.visibility = View.INVISIBLE
            _Adapter.items = listOf()
            _Adapter.notifyDataSetChanged()
            return
        }
        _Binding.tvLyricNotFound.visibility = View.INVISIBLE
        _Binding.pbLoading.visibility = View.INVISIBLE
        _Adapter.items = listOf()
        _Adapter.addAll(event.lyric.data.lrclist)
        _Adapter.notifyDataSetChanged()
        _Binding.rvList.layoutManager.let {
            it?.scrollToPosition(0)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMediaChangeEvent(event: MediaChangeEvent){
        _Binding.cpTitle.tvTitle.text = event.media.name
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMediaStateChangeEvent(event: MediaStateChangeEvent){
        _Binding.btnPlay.setImageResource(if(event.state == MediaState.Play) R.drawable.ic_pause else R.drawable.ic_playon)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMediaPositionChangeEvent(event:MediaPositionChangeEvent){
        if(!_IsTouchOfSeek && GlobalObject.CurrentMedia != null && GlobalObject.CurrentMedia?.time != "0".toLong()){
            val process = (event.position * 1.0 /GlobalObject.CurrentMedia!!.time * 100).toInt()
            _Binding.sbPosition.progress = process
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnLyricLoadingEvent(event:LyricLoadingEvent){
        _Adapter.items = listOf()
        _Adapter.notifyDataSetChanged()
        _Binding.tvLyricNotFound.visibility = View.INVISIBLE
        _Binding.pbLoading.visibility = View.VISIBLE
    }
}