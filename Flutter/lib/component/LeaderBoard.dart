import 'package:cached_network_image/cached_network_image.dart';
import 'package:music/api/ApiService.dart';
import 'package:music/common/Constant.dart';
import 'package:music/db/MySP.dart';
import 'package:music/db/tb/TB_LeaderBoard_Type.dart';
import 'package:music/model/MLeaderBoard.dart';
import 'package:music/router/MyRouter.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:music/widget/BaseLayout.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class LeaderBoard extends BaseLayout{
  @override
  BaseLayoutState<BaseLayout> getState() {
    return _LeaderBoardState();
  }

}

class _LeaderBoardState extends BaseLayoutState<LeaderBoard>{

  final double _size_pic = 120.0;

  List<MLeaderBoard> data = [];

  int _currentIndex = 0;

  @override
  Widget getChild(BuildContext context) {
    return Container(
      margin: EdgeInsets.all(15.0),
      child: Column(
        children: [
          // 排行榜类别
          Container(
            height: 50.0,
            child: ListView.builder(
              itemCount: data.length,
              scrollDirection: Axis.horizontal,
              itemBuilder: (BuildContext context,int index){
                return GestureDetector(
                  child: Container(
                    padding: EdgeInsets.only(left: 25.0,right: 25.0,top: 10.0,bottom: 10.0),
                    margin: EdgeInsets.only(left: 8.0,right: 8.0),
                    decoration: BoxDecoration(
                        color: _currentIndex == index ? Colors.blueAccent : Colors.grey.shade500,
                        borderRadius: BorderRadius.circular(10.0)
                    ),
                    child: Center(child: CommonUtil.content(text: data[index].name,color: Colors.white)),
                  ),
                  onTap: (){
                    setState(() {
                      _currentIndex = index;
                    });
                  },
                );
              },
            ),
          ),
          // 具体榜单
          Expanded(child: Container(
            margin: EdgeInsets.only(top: 25.0,bottom: 45.0),
            child: data.length != 0 ? ListView.builder(
                itemCount: data[_currentIndex].list.length,
                itemBuilder: (BuildContext context,int index){
                  return GestureDetector(
                    child: Container(
                      height: _size_pic,
                      color: Colors.transparent,
                      margin: EdgeInsets.only(left: 20.0,top: 10.0,bottom: 10.0,right: 10.0),
                      child: Row(
                        children: [
                          ClipRRect(
                            borderRadius: BorderRadius.circular(20.0),
                            child:
                              CachedNetworkImage(
                                imageUrl: data[_currentIndex].list[index].pic,
                                fit: BoxFit.cover,
                                width: _size_pic,
                                height: _size_pic,
                              )
                              // Image.network(data[_currentIndex].list[index].pic,width: _size_pic,height: _size_pic,fit: BoxFit.cover),
                          ),
                          Expanded(
                              child: Container(
                                margin: EdgeInsets.all(10.0),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Container(
                                      padding:EdgeInsets.only(left: 10.0,right: 10.0,top: 5.0,bottom: 5.0),
                                      decoration: BoxDecoration(
                                        color: Colors.blueAccent,
                                        borderRadius: BorderRadius.circular(12.0),
                                      ),
                                      child: CommonUtil.content(text: "# ${index + 1}",color: Colors.white,size: 12),
                                    ),
                                    Expanded(child: Container(
                                      margin: EdgeInsets.only(top: 10.0),
                                      child: CommonUtil.title(text: data[_currentIndex].list[index].name,size: 18),
                                    )),
                                    CommonUtil.content(text: CommonUtil.subString(data[_currentIndex].list[index].intro, 9),size: 14)
                                  ],
                                ),
                              ))
                        ],
                      ),
                    ),
                    onTap: (){
                      Navigator.pushNamed(context, MyRouter.PAGE_LEABOARD_RESULT,arguments: { Constant.KEY_SOURCEID:data[_currentIndex].list[index].sourceid,Constant.KEY_TITLE:data[_currentIndex].list[index].name });
                    },
                  );
                }) : Text(""),
          ))
        ],
      ),
    );
  }

  @override
  BaseStatus initStatus() {
    return BaseStatus.success;
  }

  @override
  void renderFinish() {

  }

  @override
  void initState2() {
    title = "排行榜";
    load();
  }


  void load() async{
    if(data.length > 0)
      return;
    List<MLeaderBoard> temp = [];
    bool flag = await CommonUtil.isDateFit(Constant.SP_LAST_UPDATE_LEADERBOARD_TIME);
    if(flag){
      temp = await TB_LeaderBoard_Type.get();
    }
    else{
      temp = await ApiService.instance.getLeaderBoard();
      TB_LeaderBoard_Type.update(temp);
      MySP.instance.setString(Constant.SP_LAST_UPDATE_LEADERBOARD_TIME, CommonUtil.getDate());
    }

    setState(() {
      status = BaseStatus.success;
      data.addAll(temp);
    });
  }

}