package com.jqwong.music.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionHelper {
    companion object{
        fun check(ctx:Context,permission:String):Boolean{
            return ActivityCompat.checkSelfPermission(ctx,permission) == PackageManager.PERMISSION_GRANTED
        }
        fun request(activity:Activity,permission: String){
            ActivityCompat.requestPermissions(activity, arrayOf(permission),100)
        }
    }
}