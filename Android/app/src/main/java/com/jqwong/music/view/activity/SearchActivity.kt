package com.jqwong.music.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.jqwong.music.app.Constant
import com.jqwong.music.databinding.ActivitySearchBinding
import com.jqwong.music.entity.HotSearchRecord
import com.jqwong.music.entity.SearchRecord
import com.jqwong.music.model.Call
import com.jqwong.music.model.MStatus
import com.jqwong.music.model.MusicCondition
import com.jqwong.music.service.KuWoService
import com.jqwong.music.service.LocalDBService
import com.jqwong.music.utils.DateTimeUtil
import java.util.Date

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    private val _musicService = KuWoService()
    private val _localDBService = LocalDBService()
    private val _searchRecordLimit = 7

    override fun Title(): String {
        return "SEARCH"
    }

    override fun initData(savedInstanceState: Bundle?) {
        initHotSearchData()
        initSearchRecord()
    }

    override fun initListener() {
        _binding.btnCancel.setOnClickListener {
            this.finish()
        }

        _binding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_SEARCH){
                val key = _binding.etSearch.text.toString()
                if(key != ""){
                    search(key)
                }
            }
            false
        })
    }

    override fun initView() {

    }

    override fun useEventBus(): Boolean {
        return false
    }


    /**
     * 初始化热搜数据
     */
    fun initHotSearchData(){
        val call = object:Call<List<String>>{
            override fun success(data: List<String>) {
                data.forEach {
                    val key = it
                    val chip = Chip(this@SearchActivity)
                    chip.text = key
                    chip.setOnClickListener {
                        toMusicListPage(MusicCondition(key,MStatus.SEARCH,key))
                    }
                    _binding.cgHotSearch.addView(chip)
                }
                _binding.tvTagHotSearch.visibility = View.VISIBLE
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }
        }
        val localEditCall = object:Call<Boolean>{
            override fun success(data: Boolean) {

            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }

        }
        val apiQueryCall = object:Call<List<String>>{
            override fun success(data: List<String>) {
                val t = mutableListOf<HotSearchRecord>()
                val date = DateTimeUtil.getDate()
                data.forEach {
                    t.add(HotSearchRecord(null,it,date))
                }
                _localDBService.deleteAll<HotSearchRecord>(localEditCall)
                _localDBService.insert(t,localEditCall)
                call.success(data)
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        }
        val localQueryCall = object: Call<List<HotSearchRecord>>{
            override fun success(data: List<HotSearchRecord>) {
                if(data.size > 0){
                    run break1@{
                        data.forEach {
                            if(it.time.equals(DateTimeUtil.getDate())){
                                val list = mutableListOf<String>()
                                data.forEach {
                                    list.add(it.Key)
                                }
                                call.success(list)
                            }
                            else{
                                _musicService.getHotSearch(apiQueryCall)
                            }
                            return@break1
                        }
                    }
                }
                else{
                    _musicService.getHotSearch(apiQueryCall)
                }
            }

            override fun error(e: Throwable) {
                call.error(e)
            }
        }
        _localDBService.loadAll(localQueryCall)
    }


    /**
     * 初始化搜索记录
     */
    fun initSearchRecord(){
        _localDBService.querySearchRecord(_searchRecordLimit,object:Call<List<SearchRecord>>{
            override fun success(data: List<SearchRecord>) {
                data.forEach {
                    val chip = Chip(this@SearchActivity)
                    val key = it.key
                    chip.text = key
                    chip.setOnClickListener {
                        toMusicListPage(MusicCondition(key,MStatus.SEARCH,key))
                    }
                    _binding.cgHistory.addView(chip)
                }
                if(_binding.cgHistory.childCount > 0){
                    _binding.tvTagHistory.visibility = View.VISIBLE
                }
            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }

        })
    }


    /**
     * 搜索
     * @param key String
     */
    fun search(key:String){
        val localEditCall = object:Call<Boolean>{
            override fun success(data: Boolean) {

            }

            override fun error(e: Throwable) {
                toast(e.message.toString())
            }

        }
        _localDBService.insert(SearchRecord(null,key,DateTimeUtil.getDate()),localEditCall)
        val chip = Chip(this)
        chip.text = key
        chip.setOnClickListener {
            toMusicListPage(MusicCondition(key,MStatus.SEARCH,key))
        }
        _binding.cgHistory.addView(chip)
        if(_binding.cgHistory.childCount > _searchRecordLimit){
            _binding.cgHistory.removeViewAt(_searchRecordLimit)
        }
        _binding.tvTagHistory.visibility = View.VISIBLE
        toMusicListPage(MusicCondition(key,MStatus.SEARCH,key))
    }


    fun toMusicListPage(params:MusicCondition){
        startActivity(Intent(this,MusicListActivity::class.java).apply {
            putExtra(Constant.ExtraKey, Gson().toJson(params))
        })
    }
}