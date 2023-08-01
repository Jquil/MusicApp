package com.jqwong.music.helper

class FunHelper {
    companion object{
        fun getName():String{
            return object{}.javaClass.enclosingMethod.name
        }
    }
}