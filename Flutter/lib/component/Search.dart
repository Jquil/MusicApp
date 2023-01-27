import 'package:music/api/ApiService.dart';
import 'package:music/common/Constant.dart';
import 'package:music/db/MySP.dart';
import 'package:music/db/tb/TB_HotSearch.dart';
import 'package:music/db/tb/TB_Search_History.dart';
import 'package:music/router/MyRouter.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:music/widget/BaseLayout.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Search extends BaseLayout {

  @override
  BaseLayoutState<BaseLayout> getState() {
    return _SearchState();
  }

}


class _SearchState extends BaseLayoutState<Search>{

  TextEditingController _tc = TextEditingController();
  List hotSearch = [],searchHistory = [];

  @override
  Widget getChild(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(left: 25.0,right: 25.0,top: 5.0),
      child: Column(
        children: [
          // 头部搜索框
          Container(
            alignment: Alignment.center,
            height: 60,
            decoration: BoxDecoration(
                color: Color.fromARGB(100, 218, 218, 218),
                borderRadius: BorderRadius.all(Radius.circular(8))
            ),
            padding: EdgeInsets.only(right: 12),
            child: TextField(
              controller: _tc,
              decoration: InputDecoration(
                hintText: "请输入歌手或歌名",
                hintStyle: TextStyle(fontSize: 16),
                border: OutlineInputBorder(borderSide: BorderSide.none),  // 取消下边框
                contentPadding: EdgeInsets.all(20),                        // 文本垂直居中
                suffixIcon: IconButton(
                    onPressed: (){
                      if(_tc.value.text == "")
                        return;
                      search();
                    },
                    icon: Icon(Icons.search,size: 28,)),
              ),
            ),
          ),
          // 搜索历史与推荐
          Container(
            width: double.infinity,
            margin: EdgeInsets.only(top: 30),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CommonUtil.title(text: "热门推荐"),
                Container(
                  margin: EdgeInsets.only(top: 5,bottom: 10),
                  child: Wrap(
                    children: widgetHotSearch()
                  ),
                ),
                CommonUtil.title(text: "搜索历史"),
                Container(
                  margin: EdgeInsets.only(top: 5,bottom: 10),
                  child: Wrap(
                    children: widgetSearchHistory(),
                  ),
                ),
              ],
            ),
          )
        ],
      ),
    );
  }

  List<Widget> widgetHotSearch(){
    List<Widget> widgets = [];
    for(int i = 0; i < hotSearch.length; i++){
      widgets.add(GestureDetector(
        onTap: (){ Navigator.pushNamed(context, MyRouter.PAGE_SEARCH_RESULT,arguments: { Constant.KEY_SEARCH: hotSearch[i].toString()}); },
        child: Container(child: Chip(label: Text("  ${hotSearch[i].toString()}  ")),margin: EdgeInsets.only(left: 5,right: 5,bottom: 10)),
      ));
    }
    return widgets;
  }


  List<Widget> widgetSearchHistory(){
    List<Widget> widgets = [];
    for(int i = 0; i < searchHistory.length; i++){
      widgets.add(GestureDetector(
        onTap: (){ Navigator.pushNamed(context, MyRouter.PAGE_SEARCH_RESULT,arguments: { Constant.KEY_SEARCH: searchHistory[i].toString()}); },
        child: Container(child: Chip(label: Text("  ${searchHistory[i].toString()}  ")),margin: EdgeInsets.only(left: 5,right: 5,bottom: 10)),
      ));
    }
    return widgets;
  }

  @override
  BaseStatus initStatus() {
    return BaseStatus.loading;
  }

  @override
  void renderFinish() {
    setState(() {
      status = BaseStatus.success;
    });
  }



  @override
  void initState2() {
    title = "搜索";
    loadHotSearch();
    loadSearchHistory();
  }

  // 搜索
  void search(){
    var key = _tc.value.text;
    TB_Search_History.insert(key);
    if(searchHistory.length >= Constant.HISTORY_ITEM_SIZE){
      searchHistory.removeAt(Constant.HISTORY_ITEM_SIZE - 1);
    }
    searchHistory.insert(0, key);

    setState(() {
      searchHistory = searchHistory;
    });
    Navigator.pushNamed(context, MyRouter.PAGE_SEARCH_RESULT,arguments: { Constant.KEY_SEARCH:key });
  }

  // 加载热搜
  void loadHotSearch() async{
    var temp = [];
    bool flag = await CommonUtil.isDateFit(Constant.SP_LAST_UPDATE_HOTSEARCH_TIME);
    if(flag){
      temp = await TB_HotSearch.get();
    }
    else{
        temp = await ApiService.instance.getHorSearch();
        MySP.instance.setString(Constant.SP_LAST_UPDATE_HOTSEARCH_TIME, CommonUtil.getDate());
        TB_HotSearch.update(temp);
    }
    setState(() {
      hotSearch.addAll(temp);
    });
  }


  // 加载历史搜索
  loadSearchHistory() async{
    var temp = await TB_Search_History.get();
    setState(() {
        searchHistory.addAll(temp);
    });
  }

}