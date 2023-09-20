package com.jqwong.music.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class WifiHelper {
    companion object{
        fun isConnected(ctx:Context):Boolean{
            val manager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val cap = manager.getNetworkCapabilities(manager.activeNetwork)
            return (cap != null) && cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
    }
}