import 'package:audioplayers/audioplayers.dart';
import 'package:music/model/MLrcList.dart';
import 'package:music/model/MSong.dart';
import 'package:flutter/cupertino.dart';

class SongProvider with ChangeNotifier{
  // 歌曲
  MSong? song;
  int? seconds;
  // 进度
  double ?progress = 0,
      milliseconds = 0;
  // 歌词
  MLrcList ?lrcList;

  // 当前歌词
  int lrcIndex = 0;

  // 播放状态
  AudioPlayerState state = AudioPlayerState.PAUSED;

  // 当前播放时间
  String time = "0:00";

  void setData(MSong s){
    song = s;
    notifyListeners();
  }

  void setSeconds(int s){
    seconds = s;
    notifyListeners();
  }

  void setProgress(int p){
    progress = p / seconds!;
    notifyListeners();
  }

  void setLrcList(MLrcList l){
    lrcList = l;
    notifyListeners();
  }

  void currentLrcIndex(int milliseconds){
    if(lrcList != null && lrcList!.lrclist.length > 0 && lrcIndex < lrcList!.lrclist.length){
      //print("${(double.parse(lrcList!.lrclist[lrcIndex].time)) * 1000} --- ${milliseconds}");
      var time = (double.parse(lrcList!.lrclist[lrcIndex].time));
      time = time * 1000;
      //print("${lrcList!.lrclist[lrcIndex].lineLyric} -- ${time} -- ${milliseconds}");
      if( milliseconds > time){
         lrcIndex++;
         notifyListeners();
       }
    }
  }

  void resetLrcIndex(){
    lrcIndex = 0;
    notifyListeners();
  }


  void setPlayState(AudioPlayerState s){
    state = s;
    notifyListeners();
  }

  void setTime(int minute,int seconds){
    int temp = seconds - minute * 60;
    time = '${minute}:${ temp < 10 ? "0${temp}" : temp }';
    notifyListeners();
  }
}