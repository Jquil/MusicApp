import 'package:music/common/Constant.dart';
import 'package:music/db/tb/TB_SongSheet.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:music/widget/BaseLayout2.dart';
import 'package:music/widget/MyCustomScrollView.dart';
import 'package:music/widget/PlayBar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';

class SongSheet extends BaseLayout2{
  @override
  BaseLayoutState2<BaseLayout2> getState() {
    return _SongSheetState();
  }

}

class _SongSheetState extends BaseLayoutState2{

  MyCustomScrollView _myCustomScrollView = MyCustomScrollView();

  int page = 1,sheetId = -1;

  bool isLoaded = false;

  String title = "";
  @override
  Widget getChild() {
    if(sheetId == -1){
      dynamic ?obj = ModalRoute.of(context)?.settings.arguments;
      sheetId = obj[Constant.KEY_SHEET_ID];
      title   = obj[Constant.KEY_TITLE];
    }
    return Scaffold(
        resizeToAvoidBottomInset: false,
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
  void load() async{
    if(_myCustomScrollView.state.loaded){
      return;
    }
    _myCustomScrollView.state.setStates((){
      _myCustomScrollView.state.loading = true;
    });

    var temp = await TB_SongSheet.load(sheetId, page, Constant.ITEM_SIZE);
    if(page == 1){
      setState(() {
        status = BaseStatus.success;
      });
      _myCustomScrollView.state.setStates((){
        _myCustomScrollView.state.pic = Constant.PICURL[CommonUtil.randomInt(Constant.PICURL.length)];
        _myCustomScrollView.state.title = title;
      });
    }

    _myCustomScrollView.state.setStates((){
      _myCustomScrollView.state.loading = false;
      _myCustomScrollView.state.data.addAll(temp);
      if(temp.length < Constant.ITEM_SIZE){
       _myCustomScrollView.state.loaded = true;
      }
    });

    page++;
  }

  @override
  void renderFinish() {
    if(!isLoaded){
      load();
      isLoaded = true;
    }
  }

}