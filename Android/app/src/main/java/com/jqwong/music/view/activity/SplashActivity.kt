package com.jqwong.music.view.activity

import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.jqwong.music.R
import com.jqwong.music.app.App
import com.jqwong.music.app.Constant
import com.jqwong.music.app.Global
import com.jqwong.music.databinding.ActivitySplashBinding
import com.jqwong.music.entity.User
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class SplashActivity:BaseActivity<ActivitySplashBinding>() {

    override fun Title(): String {
        return ""
    }

    override fun initData(savedInstanceState: Bundle?) {
        //startActivity(Intent(this@SplashActivity,MainActivity::class.java))
        jump()
    }

    override fun initListener() {

    }

    override fun initView() {

    }

    override fun useEventBus(): Boolean {
        return false
    }

    fun jump(){
        val obserable = Observable.create<Boolean> {
            val user: User?
            val sp = getSharedPreferences(Constant.USER, MODE_PRIVATE)
            val str = sp.getString(Constant.USER,null)
            if(str != null && str != ""){
                try {
                    user = Gson().fromJson(str,User::class.java)
                    Global.user = user
                    it.onNext(true)
                }
                catch (e:Exception){
                    it.onError(e)
                }
            }
            else{
                it.onNext(false)
            }
            Thread.sleep(1000)
            it.onComplete()
        }

        obserable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    toast(e.message.toString())
                }

                override fun onComplete() {
                }

                override fun onNext(t: Boolean) {
                    if(t){
                        startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                    }
                    else{
                        startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
                    }
                }

            })
    }
}