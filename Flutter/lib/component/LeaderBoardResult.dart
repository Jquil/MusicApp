import 'package:music/api/ApiService.dart';
import 'package:music/common/Constant.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/widget/BaseLayout2.dart';
import 'package:music/widget/MyCustomScrollView.dart';
import 'package:music/widget/PlayBar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';

class LeaderBoardResult extends BaseLayout2{

  @override
  BaseLayoutState2<BaseLayout2> getState() {
    return _LeaderBoardResultState();
  }

}

class _LeaderBoardResultState extends BaseLayoutState2<LeaderBoardResult>{

  MyCustomScrollView _myCustomScrollView = MyCustomScrollView();

  bool isLoaded = false;
  int page = 1,sourceid = -1;
  String title = "";


  @override
  Widget getChild() {
    if(sourceid == -1){
      dynamic ?obj = ModalRoute.of(context)?.settings.arguments;
      sourceid = int.parse(obj[Constant.KEY_SOURCEID]);
      title    = obj[Constant.KEY_TITLE];
    }
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
      return BaseStatus.loading;
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
      if(_myCustomScrollView.state.loaded){
          return;
      }
      _myCustomScrollView.state.setStates((){
          _myCustomScrollView.state.loading = true;
      });
      var temp = await ApiService.instance.getLeaderBoardResult(sourceid, page, Constant.ITEM_SIZE);
      if(page == 1){
        setState(() {
          status = BaseStatus.success;
        });
        _myCustomScrollView.state.setStates((){
          _myCustomScrollView.state.title = title;
          _myCustomScrollView.state.pic = temp.img;
        });
      }

      _myCustomScrollView.state.setStates((){
        _myCustomScrollView.state.data.addAll(temp.musicList);
        _myCustomScrollView.state.loading = false;
        if(temp.musicList.length < Constant.ITEM_SIZE){
          _myCustomScrollView.state.loaded = true;
        }
      });

      page++;
  }

}