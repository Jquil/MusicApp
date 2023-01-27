import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:music/common/Constant.dart';
import 'package:music/db/tb/TB_Cache_Song.dart';
import 'package:music/model/MCacheFile.dart';
import 'package:music/model/MSong.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/MyCache.dart';
import 'package:music/widget/BaseLayout2.dart';
import 'package:music/widget/MyCustomScrollView.dart';
import 'package:music/widget/PlayBar.dart';

class Cache extends BaseLayout2{
  @override
  BaseLayoutState2<BaseLayout2> getState() {
    return _CacheState();
  }

}

class _CacheState extends BaseLayoutState2<Cache>{

  MyCustomScrollView _myCustomScrollView = MyCustomScrollView();

  bool isLoaded = false;

  @override
  Widget getChild() {
    return Scaffold(
        body: Column(
          children: [
            Expanded(child: _myCustomScrollView),
            PlayBar()
          ],
        )
    );
  }

  @override
  void initState2() {
    _myCustomScrollView.state.state2 = this;
  }

  @override
  BaseStatus initStatus() {
    return BaseStatus.success;
  }

  @override
  void renderFinish() {
    if(!isLoaded){
      load();
      isLoaded = true;
    }
  }



  @override
  void load() async{
    if(isLoaded)
      return;
    List<MSong> temp = await MyCache.sync();
    //print(data);
    _myCustomScrollView.state.setStates((){
      _myCustomScrollView.state.pic = Constant.PICURL[0];
      _myCustomScrollView.state.title = "本地缓存";
      _myCustomScrollView.state.data.addAll(temp);
    });
  }

}