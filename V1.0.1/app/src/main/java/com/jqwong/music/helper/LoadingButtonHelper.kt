package com.jqwong.music.helper

import android.content.Context
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.jqwong.music.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CircularProgressButton.delayFinish(ctx: Context, success:Boolean, message:String,time:Long = 1500,isToast:Boolean = true){
    CoroutineScope(Dispatchers.IO).launch {
        delay(time)
        withContext(Dispatchers.Main){
            val bitmap = AppCompatResources.getDrawable(ctx,if(success) R.drawable.ic_yes else R.drawable.ic_no)!!.toBitmap()
            doneLoadingAnimation(R.color.white,bitmap)
            withContext(Dispatchers.IO){
                delay(500)
                withContext(Dispatchers.Main){
                    revertAnimation{
                        background = resources.getDrawable(R.drawable.bg_button)
                    }
                    if(isToast)
                        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}