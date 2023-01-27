import 'package:audioplayers/audioplayers.dart';
import 'package:music/api/ApiService.dart';
import 'package:music/model/MLrcList.dart';
import 'package:music/model/MSong.dart';
import 'package:music/provider/SongProvider.dart';
import 'package:flutter/cupertino.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/utils/MyCache.dart';
import 'package:music/utils/MyNotification.dart';
import 'package:provider/src/provider.dart';

class MyAudio{

  static final MyAudio instance = MyAudio._internal();

  static final AudioPlayer player = AudioPlayer();

  factory MyAudio() => instance;

  MyAudio._internal(){
    // todo
  }

  // 播放
  static play(BuildContext context,MSong song) async{
      try{
        MLrcList lrcList = await ApiService.instance.getLyric(song.musicrid);
        List time  = song.songTimeMinutes.split(":");
        int len    = time.length;
        switch(len){
          // 分钟
          case 2:
            int seconds = int.parse(time[0]) * 60 + int.parse(time[1]);
            context.read<SongProvider>().setSeconds(seconds);
            break;
          // 小时
          case 3:
            break;
        }
        //print("Time = ${song.songTimeMinutes}");
        //print(DateTime.now().millisecond);
        String path = await MyCache.getCachePath(song.musicrid);
        //print(path);
        if(path != ""){
          print("此歌曲播放缓存~");
          player.play(path,isLocal: true);
        }
        else{
          String url = await ApiService.instance.getPlayUrl(song.musicrid);
          //print("audio play url = ${url} --- muscirid = ${song.musicrid}");
          player.play(url);
        }

        context.read<SongProvider>().resetLrcIndex();
        context.read<SongProvider>().setData(song);
        context.read<SongProvider>().setLrcList(lrcList);
        context.read<SongProvider>().setProgress(0);
        context.read<SongProvider>().setTime(0, 0);
        MyNotification.instance.show(song);
      }
      catch(e){
        Fluttertoast.showToast(msg: e.toString());
      }
  }

  // 暂停
  static void pause(BuildContext context){
    if(context.read<SongProvider>().song != null){
      player.pause();
    }
    else{
      Fluttertoast.showToast(msg: "nonono~");
    }
  }

  static void resume(BuildContext context){
    if(context.read<SongProvider>().song != null){
      player.resume();
    }
    else{
      Fluttertoast.showToast(msg: "nonono~");
    }
  }
}