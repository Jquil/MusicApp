package com.jqwong.music.page.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.Gson
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.api.service.MusicApiService
import com.jqwong.music.databinding.ActivityLoginBinding
import com.jqwong.music.model.GlobalObject
import com.jqwong.music.repository.entity.User
import com.jqwong.music.service.RemoteService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginActivity : BaseActivity() {

    private lateinit var _Binding : ActivityLoginBinding


    private val _Service = RemoteService()

    override fun InitView() {
        
    }

    override fun InitData() {
    }

    override fun InitListener() {
        _Binding.btnLogin.setOnClickListener {
            val name = _Binding.etUsername.text.toString()
            val pass = _Binding.etPassword.text.toString()
            if(name == "" || pass == "")
                return@setOnClickListener

            it.isEnabled = false
            val imm = this@LoginActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this@LoginActivity.window.decorView.windowToken, 0)

            _Service.Login(name,pass,object:HttpCall<RemoteService.RemoteResult<User>>{
                override fun onError(e: Throwable) {
                    it.isEnabled = true
                    Toast.makeText(this@LoginActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
                }

                override fun OnSuccess(t: RemoteService.RemoteResult<User>) {
                    Toast.makeText(this@LoginActivity,t.desc,Toast.LENGTH_SHORT).show()
                    it.isEnabled = true
                    if(t.code == RemoteService.RemoteResultCode.OK){
                        _Binding.btnLogin.start()
                        GlobalObject.User = com.jqwong.music.model.User(t.data!!.Id!!,t.data!!.Name,"")
                        SyncRemoteData()
                    }
                }
            })

        }
    }

    override fun SetContentView(savedInstanceState: Bundle?) {
        _Binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_Binding.root)
    }

    override fun Destory() {
        
    }


    private fun SyncRemoteData(){

        //Toast.makeText(this@LoginActivity,"Please wait a moment, data is being synchronized...",Toast.LENGTH_LONG).show()
        val cacheTask = Observable.create<Boolean> {
            val sp = getSharedPreferences(GlobalObject.UserKey, MODE_PRIVATE)
            with(sp.edit()){
                putString(GlobalObject.UserKey,Gson().toJson(GlobalObject.User))
                apply()
            }
            Thread.sleep(1000)
            it.onNext(true)
            it.onComplete()
        }

        var taskCount = 0
        val call:HttpCall<Boolean> = object:HttpCall<Boolean>{
            override fun onError(e: Throwable) {
                taskCount++
            }

            override fun OnSuccess(t: Boolean) {
                taskCount++
               if(taskCount == 5){
                   cacheTask.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                       .subscribe(object:MusicApiService.BaseObserver<Boolean>{
                           override fun onSubscribe(d: Disposable) {
                           }

                           override fun onNext(t: Boolean) {
                               startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                               //it.isEnabled = true
                           }

                           override fun onError(e: Throwable) {
                           }

                           override fun onComplete() {
                           }
                       })
               }
            }
        }

        val service = RemoteService()
        val userId = GlobalObject.User?.id!!
        service.SyncSheet(userId,call)
        service.SyncArtist(userId,call)
        service.SyncFavorite(userId,call)
        service.SyncSheetInfo(userId,call)
        service.SyncMedia(call)
    }
}