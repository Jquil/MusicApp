import 'package:music/api/ApiService.dart';
import 'package:music/common/Constant.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/widget/BaseLayout2.dart';
import 'package:music/widget/MyCustomScrollView.dart';
import 'package:music/widget/PlayBar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';

class Singer extends BaseLayout2{

  @override
  BaseLayoutState2<BaseLayout2> getState() {
    return _SingerState();
  }

}

class _SingerState extends BaseLayoutState2<Singer>{

  MyCustomScrollView _myCustomScrollView = MyCustomScrollView();

  String singer = "",pic = "";
  int artistid = -1,page = 1;
  bool isLoaded = false;

  @override
  Widget getChild() {
    if(artistid == -1){
      dynamic ?obj = ModalRoute.of(context)?.settings.arguments;
      singer   = obj[Constant.KEY_SINGER];
      pic      = obj[Constant.KEY_PIC];
      artistid = int.parse(obj[Constant.KEY_ARTISTID].toString());
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
     var temp = await ApiService.instance.getSongBySinger(artistid, page, Constant.ITEM_SIZE);

    if(page == 1){
      setState(() {
        status = BaseStatus.success;
      });
      _myCustomScrollView.state.setStates((){
        _myCustomScrollView.state.pic = pic;
        _myCustomScrollView.state.title = singer;
        _myCustomScrollView.state.data.addAll(temp);
      });
    }

    _myCustomScrollView.state.setStates((){
      _myCustomScrollView.state.data.addAll(temp);
      _myCustomScrollView.state.loading = false;
      if(temp.length < Constant.ITEM_SIZE){
        _myCustomScrollView.state.loaded = true;
      }
    });

    page++;
  }



}