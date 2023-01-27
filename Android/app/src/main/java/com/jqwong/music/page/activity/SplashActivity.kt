package com.jqwong.music.page.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.jqwong.music.api.service.MusicApiService
import com.jqwong.music.databinding.ActivitySplashBinding
import com.jqwong.music.model.GlobalObject
import com.jqwong.music.model.User
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashActivity : BaseActivity() {
    
    private lateinit var _Binding:ActivitySplashBinding
    
    override fun InitView() {
        
    }

    override fun InitData() {
        // finish app init edit and init user
        val obserable = Observable.create<Boolean> {
            val user:User?
            val sp = getSharedPreferences(GlobalObject.UserKey, MODE_PRIVATE)
            val str = sp.getString(GlobalObject.UserKey,null)
            if(str != null){
                try {
                    user = Gson().fromJson(str,User::class.java)
                    GlobalObject.User = user
                }
                catch (e:Exception){}
            }
            Thread.sleep(1000)
            it.onNext(true)
            it.onComplete()
        }

        obserable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<Boolean>{
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Boolean) {
                    if(GlobalObject.User == null){
                        startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
                    }
                    else{
                        startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }

    override fun InitListener() {
        
    }

    override fun SetContentView(savedInstanceState: Bundle?) {
        _Binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_Binding.root)
    }

    override fun Destory() {
        
    }
}