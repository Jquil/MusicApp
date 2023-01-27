package com.jqwong.music.page.activity

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.databinding.ActivitySearchBinding
import com.jqwong.music.model.GlobalObject
import com.jqwong.music.model.ResultCondition
import com.jqwong.music.model.ResultKey
import com.jqwong.music.repository.entity.History
import com.jqwong.music.service.HistoryService
import com.jqwong.music.service.HotSearchService

class SearchActivity : BaseActivity() {

    private lateinit var _Binding : ActivitySearchBinding

    private var _HistoryService : HistoryService

    private var _HotSearchService : HotSearchService

    private var _HistoryList:List<History>

    private var _MaxLoadHistoryDataSize:Int = 10

    init {
        _HistoryService = HistoryService()
        _HistoryList = _HistoryService.GetAll(_MaxLoadHistoryDataSize)
        _HotSearchService = HotSearchService();
    }



    override fun InitListener(){

        _Binding.btnCancel.setOnClickListener {
            this.finish()
        }

        _Binding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_SEARCH)
                Search()
            false
        })
    }

    override fun SetContentView(savedInstanceState: Bundle?) {
        _Binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(_Binding.root)
    }

    override fun Destory() {

    }


    override fun InitView(){

        _Binding.cpTitle.tvTitle.text = "Search"

        if(_HistoryList != null){
            _HistoryList.forEach { history ->
                _Binding.cgHistory.addView(NewHistoryChip(history.key))
            }
        }

        _HotSearchService.GetHotSearchList(object:HttpCall<List<String>>{
            override fun onError(e: Throwable) {
                Toast.makeText(this@SearchActivity,e.message,Toast.LENGTH_SHORT).show()
                _Binding.cgHotSearch.addView(NewHotSearchChip(e.message.toString()))
            }

            override fun OnSuccess(t: List<String>) {
                t.forEach {
                    _Binding.cgHotSearch.addView(NewHotSearchChip(it))
                }
            }
        })
    }

    override fun InitData() {

    }


    fun Search(){
        val key = _Binding.etSearch.text.toString()
        if(key == "")
            return
        _HistoryService.Add(key)
        if(_Binding.cgHistory.childCount == _MaxLoadHistoryDataSize)
            _Binding.cgHistory.removeViewAt(_MaxLoadHistoryDataSize-1)
        _Binding.cgHistory.addView(NewHistoryChip(key),0)
        ToResultActivity(key)
    }


    fun NewHistoryChip(key:String):Chip{
        var chip = Chip(this)
        chip.text = key
        chip.setOnClickListener {
            ToResultActivity(key)
        }
        chip.setOnLongClickListener {
            _HistoryService.Delete(key)
            _Binding.cgHistory.removeView(it)
            true
        }
        return chip
    }



    fun NewHotSearchChip(key:String) : Chip{
        var chip = Chip(this)
        chip.text = key
        chip.setOnClickListener {
            ToResultActivity(key)
        }
        return chip
    }



    fun ToResultActivity(key:String){
        val req = ResultCondition(ResultKey.Search,key,key)
        startActivity(Intent(this,ResultActivity::class.java).apply {
            putExtra(GlobalObject.ExtraKey,Gson().toJson(req))
        })
    }
}