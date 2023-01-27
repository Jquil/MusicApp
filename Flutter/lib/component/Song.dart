import 'dart:ui';

import 'package:audioplayers/audioplayers.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/api/ApiService.dart';
import 'package:music/common/Constant.dart';
import 'package:music/db/tb/TB_SongSheet.dart';
import 'package:music/model/MSinger.dart';
import 'package:music/provider/SongProvider.dart';
import 'package:music/provider/SongQueueProvider.dart';
import 'package:music/router/MyRouter.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:music/utils/MyAudio.dart';
import 'package:music/utils/MyCache.dart';
import 'package:music/widget/CommonWidget.dart';
import 'package:provider/src/provider.dart';

class Song extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _SongState();
  }

}

class _SongState extends State<Song>{

  double ?size_pic = 0;

  bool isFirstLoad = true;

  double ?itemHeight = 70;

  ScrollController _sc = ScrollController();

  int tempIndex = 0;


  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    if(isFirstLoad){
      size_pic = MediaQuery.of(context).size.width * 0.8;
      isFirstLoad = false;
    }
    var provider = context.watch<SongProvider>();
    if(tempIndex != provider.lrcIndex){
      tempIndex = provider.lrcIndex;
      _sc.animateTo((tempIndex - 2) * itemHeight!, duration: Duration(seconds: 1), curve: Curves.ease);
    }
    return Scaffold(
        body: Container(
          width: double.infinity,
          height: double.infinity,
          child: Container(
            decoration: BoxDecoration(
              image: DecorationImage(image: NetworkImage(provider.song!.pic120),fit: BoxFit.cover)
            ),
            child: ClipRect(
              child: BackdropFilter(
                filter: ImageFilter.blur(sigmaX: 100,sigmaY: 100),
                child: SafeArea(
                    child: Container(
                      margin: EdgeInsets.only(left: 25.0,right: 25.0,top: 10),
                      child: Column(
                        children: [
                          Row(
                            children: [
                              Container( padding: EdgeInsets.only(right: 10), child: IconButton(onPressed: (){ Navigator.of(context).pop();  }, icon: Icon(Icons.keyboard_arrow_down_outlined,color: Colors.white,size: 30,)),),
                              Expanded(child: CommonUtil.content(text: provider.song == null ? "" : "${CommonUtil.subString(provider.song!.name, 5)}(${CommonUtil.subString(provider.song!.artist, 6)})",color: Colors.white,size: 22),),
                              PopupMenuButton(
                                  icon: Icon(Icons.more_vert,color: Colors.white,),
                                  itemBuilder: (context) => <PopupMenuEntry<String>>[
                                      PopupMenuItem(child: CommonUtil.content(text: "查看该歌手",color: CommonUtil.getStateLayoutBG2(context)),value: "1",),
                                      PopupMenuItem(child: CommonUtil.content(text: "收藏到歌单",color: CommonUtil.getStateLayoutBG2(context)),value: "2",),
                                      PopupMenuItem(child: CommonUtil.content(text: "添加至缓存",color: CommonUtil.getStateLayoutBG2(context)),value: "3",),
                                  ],
                                onSelected: (v){
                                    switch(int.parse(v.toString())){
                                      // 查看歌手
                                      case 1:
                                        //var pic = provider.song!.pic120.replaceAll("/120/", "/700/");
                                        //Navigator.pushNamed(context, MyRouter.PAGE_SINGER,arguments: { Constant.KEY_ARTISTID:provider.song!.artistid,Constant.KEY_SINGER:provider.song!.artist,Constant.KEY_PIC:pic });
                                        querySinger(provider.song!.artistid);
                                        break;
                                      // 收藏到歌单
                                      case 2:
                                        CommonWidget.showSongSheetDialog(context,(int sheetId){
                                          TB_SongSheet.collect(context, sheetId, provider.song!);
                                        });
                                        break;
                                      // 缓存
                                      case 3:
                                        MyCache.cache1(provider.song!,(flag){
                                          String tip = "缓存成功~";
                                          if(!flag){
                                            tip = "已存在该缓存~";
                                          }
                                          Fluttertoast.showToast(msg: tip);
                                        });
                                        break;
                                    }
                                },
                              )
                            ],
                          ),
                          Container(
                            margin: EdgeInsets.only(top: 35,bottom: 35),
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(double.parse(size_pic.toString())),
                              boxShadow: [
                                BoxShadow(
                                    color: Colors.black26,
                                    offset: Offset(0, -6.0), //阴影xy轴偏移量
                                    blurRadius: 50.0, //阴影模糊程度
                                    spreadRadius: 5.0 //阴影扩散程度
                                )
                              ],
                            ),
                            child: ClipRRect(
                              borderRadius: BorderRadius.circular(double.parse(size_pic.toString())),
                              child: Image.network(CommonUtil.pic120to700(provider.song!.pic),width: size_pic,height: size_pic,fit: BoxFit.cover),
                            ),
                          ),
                          // 歌词
                          Container(
                            height: 210,
                            child: ListView.builder(
                                controller: _sc,
                                itemBuilder: (BuildContext context,int index){
                                  return Container(
                                    height: itemHeight,
                                    child: Center(child: CommonUtil.content(text: provider.lrcList?.lrclist[index].lineLyric,color: (tempIndex - 1) == index ? Colors.white : Colors.white70,size: (tempIndex - 1) == index ? 20 : 16),),
                                  );
                                },
                                itemCount: provider.lrcList?.lrclist.length,
                            )
                          ),
                          // 进度
                          Expanded(child:
                          Column(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              // 进度条
                              Slider(
                                  inactiveColor: Color(0xffe9ecef),
                                  activeColor: Color(0xff6c757d),
                                  max: 100,
                                  value: (provider.progress!) * 100,
                                  onChanged: (v){
                                    // todo
                                  }),
                              // 播放时间
                              Container(
                                margin: EdgeInsets.only(left: 25,right: 25),
                                child: Row(
                                  children: [
                                    CommonUtil.content(text: provider.time,color: Color(0xfff8f9fa)),
                                    Expanded(child: Text("")),
                                    CommonUtil.content(text: provider.song!.songTimeMinutes,color: Color(0xfff8f9fa)),
                                  ],
                                ),
                              )
                            ],
                          )),
                          // 底部按钮
                          Container(
                            margin: EdgeInsets.only(bottom: 25),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                // 上一首
                                IconButton(onPressed: (){
                                  context.read<SongQueueProvider>().pre(context);
                                }, icon: Icon(Icons.skip_previous_sharp,color: Colors.white,),iconSize: 36,),
                                // 播放/暂停
                                IconButton(onPressed: (){
                                  provider.state == AudioPlayerState.PAUSED ? MyAudio.resume(context) : MyAudio.pause(context);
                                }, icon: Icon(provider.state == AudioPlayerState.PAUSED ? Icons.play_circle_outline : Icons.pause_circle_outline,color: Colors.white),iconSize: 66,),
                                // 下一首
                                IconButton(onPressed: (){
                                  context.read<SongQueueProvider>().next(context);
                                }, icon: Icon(Icons.skip_next_sharp,color: Colors.white),iconSize: 36,),
                              ],
                            ),
                          )
                        ],
                      ),
                    ))
              ),
            )
          ),
        )
      );
  }


  // 查看歌手
  void querySinger(int artistid) async{
    MSinger? singer = await ApiService.instance.getSingerInfo(artistid);
    if(singer != null){
      var pic = singer.pic.replaceAll("/120/", "/700/");
      Navigator.pushNamed(context, MyRouter.PAGE_SINGER,arguments: { Constant.KEY_ARTISTID:singer.id,Constant.KEY_SINGER:singer.name,Constant.KEY_PIC:pic,Constant.KEY_DATA:singer });
    }
    else{
      Fluttertoast.showToast(msg: "出现位置错误，找不到阿~");
    }
  }
}