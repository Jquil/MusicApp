import 'package:music/api/ApiService.dart';
import 'package:music/common/Constant.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:music/widget/BaseLayout2.dart';
import 'package:music/widget/MyCustomScrollView.dart';
import 'package:music/widget/PlayBar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';

class SearchResult extends BaseLayout2{
  @override
  BaseLayoutState2<BaseLayout2> getState() {
    return _SearchResultState();
  }

}

class _SearchResultState extends BaseLayoutState2<SearchResult>{
  MyCustomScrollView _myCustomScrollView = MyCustomScrollView();
  String key = "";
  bool isLoaded = false;
  int page = 1;

  @override
  Widget getChild() {
    if(key == ""){
      dynamic ?obj = ModalRoute.of(context)?.settings.arguments;
      key = obj[Constant.KEY_SEARCH];
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
    var temp = await ApiService.instance.search(key, page, Constant.ITEM_SIZE);
    if(page == 1){
      setState(() {
        status = BaseStatus.success;
      });
      _myCustomScrollView.state.setStates((){
        _myCustomScrollView.state.pic = Constant.PICURL[CommonUtil.randomInt(Constant.PICURL.length)];
        _myCustomScrollView.state.title = key;
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