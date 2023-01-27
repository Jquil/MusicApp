import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/model/MSong.dart';
import 'package:flutter/cupertino.dart';
import 'package:music/utils/MyAudio.dart';

class SongQueueProvider with ChangeNotifier{
  List<MSong> queue = [];
  int currentIndex = 0,lastTime = 0;

  void setQueue(List<MSong> list){
    queue = list;
    currentIndex = 0;
    print("setQueue：${queue[0].name}");
    notifyListeners();
  }

  void next(BuildContext context){
    var temp = DateTime.now().millisecondsSinceEpoch;
    if(temp - lastTime < 1000){
      return;
    }
    lastTime = temp;
    if(currentIndex + 1 < queue.length){
      currentIndex++;
      //print("next：index = ${currentIndex} -- name = ${queue[currentIndex].name}");
      MyAudio.play(context, queue[currentIndex]);
      notifyListeners();
    }
    else{
      Fluttertoast.showToast(msg: "歌曲已全部播放完成~");
    }
  }


  void pre(BuildContext context){
    if(currentIndex != 0){
      currentIndex--;
      MyAudio.play(context, queue[currentIndex]);
      notifyListeners();
    }
    else{
      Fluttertoast.showToast(msg: "已经是第一首啦~");
    }
  }
}