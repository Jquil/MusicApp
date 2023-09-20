//package com.jqwong.music.helper
//
//import com.arthenica.mobileffmpeg.Config
//import com.arthenica.mobileffmpeg.FFmpeg
//
///**
// * @author: Jq
// * @date: 7/28/2023
// */
//class FFmPegHelper {
//    companion object{
//        fun getAudio(mvUrl:String,path:String):Pair<Boolean,String>{
//            val cmd = "-i $mvUrl -vn -acodec copy $path"
//            val rc = FFmpeg.execute(cmd)
//            var success = false
//            var message = ""
//            if (rc == Config.RETURN_CODE_SUCCESS) {
//                success = true
//            }
//            else if (rc == Config.RETURN_CODE_CANCEL) {
//                message = "Command execution cancelled by user."
//            } else {
//                //message = String.format("Command execution failed with rc=%d and the output below.")
//            }
//            return Pair(success,message)
//        }
//    }
//}