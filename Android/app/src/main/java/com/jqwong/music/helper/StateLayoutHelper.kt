package com.jqwong.music.helper

import android.widget.TextView
import com.drake.statelayout.StateLayout
import com.jqwong.music.R
import com.jqwong.music.model.ExceptionLog

fun StateLayout.startAnimation(){
    // 先将视图隐藏然后在800毫秒内渐变显示视图
    animate().setDuration(0).alpha(0F).withEndAction {
        animate().setDuration(800).alpha(1F)
    }
}

fun StateLayout.error(log:ExceptionLog){
    apply {
        onError {
            startAnimation()
        }
        showError()
        val tv = findViewById<TextView>(R.id.tv_error)
        val builder = StringBuilder()
        builder.appendLine("stackTrace:")
        log.exception.stackTrace.forEach {
            builder.appendLine("${it.className}.${it.methodName}.${it.fileName}:${it.lineNumber}")
        }
        builder.appendLine("message:")
        builder.appendLine(log.exception.message)
        tv.text = builder.toString()
    }
}

fun StateLayout.content(){
    apply {
        onContent {
            this@apply.startAnimation()
        }
        showContent()
    }
}

fun StateLayout.empty(message:String){
    apply {
        onEmpty {
            this@apply.startAnimation()
        }
        showEmpty()
        val tv = findViewById<TextView>(R.id.tv_empty)
        tv.text = message
    }
}