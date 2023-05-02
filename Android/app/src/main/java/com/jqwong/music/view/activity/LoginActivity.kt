package com.jqwong.music.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.jqwong.music.app.App
import com.jqwong.music.app.Constant
import com.jqwong.music.app.Global
import com.jqwong.music.databinding.ActivityLoginBinding
import com.jqwong.music.entity.Artist
import com.jqwong.music.entity.FavoriteArtist
import com.jqwong.music.entity.FavoriteArtist2
import com.jqwong.music.entity.FavoriteMedia
import com.jqwong.music.entity.Media
import com.jqwong.music.entity.Sheet
import com.jqwong.music.entity.SheetInfo
import com.jqwong.music.entity.User
import com.jqwong.music.model.Call
import com.jqwong.music.service.LocalDBService
import com.jqwong.music.service.MemfireDBService
import java.util.UUID

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class LoginActivity:BaseActivity<ActivityLoginBinding>(){

    private val _service = MemfireDBService()

    override fun Title(): String {
        return "LOGIN"
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        _binding.btnLogin.setOnClickListener {
            val userName = _binding.etUsername.text.toString()
            val password = _binding.etPassword.text.toString()
            if(userName == "" || password == ""){
                toast("Please enter username and password,thanks")
                return@setOnClickListener
            }
            val imm = this@LoginActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this@LoginActivity.window.decorView.windowToken, 0)
            it.isEnabled = false
            _binding.btnLogin.start()
            login(userName, password,object:Call<Boolean>{
                override fun success(data: Boolean) {
                    it.isEnabled = true
                }

                override fun error(e: Throwable) {
                    toast(e.message.toString())
                    it.isEnabled = true
                    _binding.btnLogin.cancel()
                }
            })
        }

    }

    override fun initView() {

    }

    override fun useEventBus(): Boolean {
        return false
    }

    /**
     * 登录, 如果用户不存在自动注册
     * @param userName String
     * @param password String
     */
    fun login(userName:String,password:String,call: Call<Boolean>){
        _service.queryUserByName(userName,object:Call<User?>{
            override fun success(data: User?) {
                if(data != null){
                    if(data.Password.equals(password)){
                        Global.user = data
                        syncUserData(data,object:Call<Boolean>{
                            override fun success(data: Boolean) {
                                saveUserData(Global.user!!)
                                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                                finish()
                            }

                            override fun error(e: Throwable) {
                                call.error(e)
                            }
                        })
                    }
                    else{
                        toast("password error")
                    }
                }
                else{
                    val user = User(null,userName,password,false,UUID.randomUUID().toString())
                    _service.insertUser(user,object:Call<Boolean>{
                        override fun success(data: Boolean) {
                            Global.user = user
                            saveUserData(user)
                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                            finish()
                        }

                        override fun error(e: Throwable) {
                            call.error(e)
                        }

                    })
                }
                call.success(true)
            }

            override fun error(e: Throwable) {
                call.error(e)
            }
        })
    }

    fun saveUserData(user:User){
        val sp = getSharedPreferences(Constant.USER, MODE_PRIVATE)
        with(sp.edit()){
            putString(Constant.USER, Gson().toJson(user))
            apply()
        }
    }


    /**
     * 同步用户数据
     */
    fun syncUserData(user: User,call: Call<Boolean>){

        /**
         * table:
         * [1] artist
         * [2] media
         * [3] favorite_media
         * [4] favorite_artist
         * [5] sheet
         * [6] sheet_info
         *
         * [1] reset local database
         * sync data flow:
         * Set<int> medias
         * [2] sync favorite_artist
         *    query favorite_artist where UserUUID
         *    then query artist' ArtistId = favorite_artist's ArtistId order by favorite_artist's Id
         *    return artist_infos
         * [3] sync sheet
         *     query sheet where UserUUID
         *     return sheet
         * [4] sync sheet_info
         *     query sheetInfo where UserUUID order by Id
         *     foreach get sheetInfo.rid to add medias
         *     return infos
         * [5] sync favorite_media
         *     query favorite_media where UserUUID
         *     foreach get item.rid to add medias
         *     return favoriteMedias
         * [6] sync media
         *     query media where rid in (medias)
         *     return medias
         */
        var i = 0
        val taskCount = 5
        val mediasSet = mutableSetOf<Int>()
        val localDBService = LocalDBService()
        val c = object:Call<Boolean>{
            override fun success(data: Boolean) {
                i++
                if(i == taskCount){
                    val builder = StringBuilder()
                    mediasSet.forEach {
                        builder.append("${it},")
                    }
                    if(builder.isNotEmpty()){
                        builder.deleteCharAt(builder.length-1)
                        _service.queryMediasByRid(builder.toString(),object:Call<List<Media>>{
                            override fun success(data: List<Media>) {
                                data.forEach {
                                    it.id = null
                                }
                                localDBService.insert(data,object:Call<Boolean>{
                                    override fun success(data: Boolean) {
                                        call.success(true)
                                    }

                                    override fun error(e: Throwable) {
                                        call.error(e)
                                    }

                                })
                            }

                            override fun error(e: Throwable) {
                                call.error(e)
                            }

                        })
                    }
                    else{
                        call.success(true)
                    }
                }
            }
            override fun error(e: Throwable) {
                call.error(e)
            }
        }

        // [1] reset local database
        localDBService.reset(c)

        // [2] sync favorite_artist
        _service.queryFavoriteArtists(user.UUID,object:Call<List<FavoriteArtist2>>{
            override fun success(data: List<FavoriteArtist2>) {
                if(data.isEmpty()){
                    c.success(true)
                    return
                }
                val builder = StringBuilder()
                data.forEach {
                    builder.append("${it.ArtistId},")
                }
                builder.deleteCharAt(builder.length-1)
                _service.queryAristById(builder.toString(),object:Call<List<Artist>>{
                    override fun success(data: List<Artist>) {
                        if(data.isEmpty()){
                            c.success(true)
                            return
                        }
                        val entities = mutableListOf<FavoriteArtist>()
                        data.forEach {
                            entities.add(FavoriteArtist(null,it.Name,it.Pic,it.ArtistId))
                        }
                        localDBService.insert(entities,c)
                    }

                    override fun error(e: Throwable) {
                        c.error(e)
                    }

                })
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        })

        // [3] sync sheet
        _service.querySheetByUserUUID(user.UUID,object:Call<List<Sheet>>{
            override fun success(data: List<Sheet>) {
                if(data.isEmpty()){
                    c.success(true)
                    return
                }
                val entities = mutableListOf<Sheet>()
                data.forEach {
                    entities.add(Sheet(null,it.name,user.UUID,it.token))
                }
                localDBService.insert(entities,c)
            }

            override fun error(e: Throwable) {
                c.error(e)
            }

        })

        // [4] sync sheet_info
        _service.querySheetInfoByUserUUID(user.UUID,object:Call<List<SheetInfo>>{
            override fun success(data: List<SheetInfo>) {
                if(data.isEmpty()){
                    c.success(true)
                    return
                }
                val entities = mutableListOf<SheetInfo>()
                data.forEach {
                    entities.add(SheetInfo(null,it.userUUID,it.sheetToken,it.rid))
                    mediasSet.add(it.rid.toInt())
                }
                localDBService.insert(entities,c)
            }

            override fun error(e: Throwable) {
                c.error(e)
            }

        })

        // [5] sync favorite_medias
        _service.queryFavoriteMedia(user.UUID,object:Call<List<FavoriteMedia>>{
            override fun success(data: List<FavoriteMedia>) {
                if(data.isEmpty()){
                    c.success(true)
                    return
                }
                val entities = mutableListOf<FavoriteMedia>()
                data.forEach {
                    entities.add(FavoriteMedia(null,it.rid,it.UserUUID))
                    mediasSet.add(it.rid)
                }
                localDBService.insert(entities,c)
            }

            override fun error(e: Throwable) {
                c.error(e)
            }

        })
    }
}