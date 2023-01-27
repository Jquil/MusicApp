import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/api/ApiService.dart';
import 'package:music/common/Constant.dart';
import 'package:music/db/tb/TB_Singer.dart';
import 'package:music/db/tb/TB_SongSheet.dart';
import 'package:music/db/tb/TB_SongSheet_Info.dart';
import 'package:music/model/MSinger.dart';
import 'package:music/model/MSong.dart';
import 'package:music/provider/SongQueueProvider.dart';
import 'package:music/provider/SongSheetProvider.dart';
import 'package:music/router/MyRouter.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:music/utils/MyAudio.dart';
import 'package:music/utils/MyCache.dart';
import 'package:music/widget/BaseLayout2.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:music/widget/CommonWidget.dart';
import 'package:provider/src/provider.dart';

class MyCustomScrollView extends StatefulWidget{

  MyCustomScrollView();

  _MyCustomScrollViewState state = _MyCustomScrollViewState();

  @override
  State createState() {
    return state;
  }
}

class _MyCustomScrollViewState extends State<MyCustomScrollView>{

  final double _item_pic = 50,_item_height = 80,_header_pic = 350;

  String title = "",pic = "";
  ScrollController _sc = ScrollController();

  List<MSong> data = [];

  bool loading = false,loaded = false;

  BaseLayoutState2 ? state2;

  int sheetId = -1; // 用于移出歌单

  MSinger? singer;  // 用于收藏歌手

  @override
  void initState() {
    super.initState();
    _sc.addListener(() {
      if(_sc.position.pixels == _sc.position.maxScrollExtent){
        state2?.load();
      }
    });
  }


  @override
  void dispose() {
    _sc.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      body: CustomScrollView(
        controller: _sc,
        physics: BouncingScrollPhysics(),
        slivers: [
          SliverAppBar(
            expandedHeight: _header_pic,
            pinned: true,
            leading: Container( padding: EdgeInsets.only(left: 20.0), child: IconButton(onPressed: (){ Navigator.of(context).pop();  }, icon: Icon(Icons.keyboard_arrow_down_outlined)),),
            flexibleSpace: FlexibleSpaceBar(
              title: Text(title),
              background: pic == "" ? Text("") : Image.network(pic,fit: BoxFit.cover,height: _header_pic),
            ),
            actions: [
              ModalRoute.of(context)!.settings.name == MyRouter.PAGE_SINGER
                  ? PopupMenuButton(
                        itemBuilder: (context) => <PopupMenuEntry<String>>[
                          PopupMenuItem(child: Text("收藏歌手"),value: "1",),
                        ],
                        onSelected: (value){
                          switch(int.parse(value.toString())){
                            // 收藏歌手
                            case 1:
                              collectSinger();
                              break;
                          }
                        },
                    )

                  : ModalRoute.of(context)!.settings.name == MyRouter.PAGE_SONG_SHEET
                    ? PopupMenuButton(
                        itemBuilder: (context) => <PopupMenuEntry<String>>[
                          PopupMenuItem(child: Text("修改名称"),value: "1",),
                          PopupMenuItem(child: Text("删除歌单"),value: "2",),
                        ],
                        onSelected: (v){
                          switch(int.parse(v.toString())){
                            // 修改名称
                            case 1:
                              CommonWidget.updateSheetDialog(context, TextEditingController(), title, (sheetName){
                                  getSheetId();
                                  TB_SongSheet_Info.update(sheetId, sheetName);
                                  context.read<SongSheetProvider>().updateSheetName(sheetId, sheetName);
                                  setState(() {
                                    title = sheetName;
                                  });
                                  Fluttertoast.showToast(msg: "修改成功~");
                              });
                              break;
                            // 删除歌单
                            case 2:
                              dropSheet();
                              break;
                          }
                        },
                      )
                    : Text("")
            ],
          ),
          SliverFixedExtentList(
              delegate: SliverChildBuilderDelegate((context,index){
                if(index == data.length){
                  return Center(
                    // 分loading 还是 loaded
                    child: loading ? Image.asset("assets/images/loading.gif",width: _item_pic - 5,) : loaded ? Text("数据全部加载完成~") : Text(""),
                  );
                }
                else{
                  return Container(
                      height: _item_height,
                      margin: EdgeInsets.only(top: 15),
                      child: ListTile(
                        leading: ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          child: Image.network(data[index].pic,width: _item_pic,height: _item_pic,fit: BoxFit.cover),
                        ),
                        title: CommonUtil.title(text: CommonUtil.subString(data[index].name, 8)),
                        subtitle: CommonUtil.content(text: data[index].album == "" ? CommonUtil.subString(data[index].artist, 5) : CommonUtil.subString(data[index].artist, 5) + " - " + CommonUtil.subString(data[index].album, 8),size: 12),
                        trailing: PopupMenuButton(
                          itemBuilder: (context) => <PopupMenuEntry<String>>[
                            PopupMenuItem(child: CommonUtil.content(text: "查看该歌手"),value: "1",),
                            PopupMenuItem(child: CommonUtil.content(text: "收藏至歌单"),value: "2",),
                            ModalRoute.of(context)!.settings.name != MyRouter.PAGE_SONG_SHEET
                                          ? PopupMenuItem(child: CommonUtil.content(text: "添加至喜欢"),value: "3",)
                                          : PopupMenuItem(child: CommonUtil.content(text: "移出歌单"),value: "4",),
                            ModalRoute.of(context)!.settings.name != MyRouter.PAGE_CACHE
                                          ? PopupMenuItem(child: CommonUtil.content(text: "缓存"),value: "5",)
                                          : PopupMenuItem(child: CommonUtil.content(text: "删除缓存"),value: "6",),
                          ],
                          onSelected: (v){
                            switch(int.parse(v.toString())){
                                // 查看歌手
                                case 1:
                                  // https://img1.kuwo.cn/star/albumcover/120/91/45/3727960159.jpg
                                  querySinger(data[index].artistid);
                                  break;
                                // 收藏至歌单
                                case 2:
                                  CommonWidget.showSongSheetDialog(context,(int sheetId){
                                    TB_SongSheet.collect(context, sheetId, data[index]);
                                  });
                                  break;
                                // 添加至喜欢
                                case 3:
                                  TB_SongSheet.collect(context, 1, data[index]);
                                  break;
                                // 移出歌单
                                case 4:
                                  getSheetId();
                                  TB_SongSheet.remove(context, sheetId, data[index]);
                                  setState(() {
                                    data.removeAt(index);
                                  });
                                  break;
                                // 缓存
                                case 5:
                                  MyCache.cache1(data[index],(flag){
                                    String tip = "缓存成功~";
                                    if(!flag){
                                      tip = "已存在该缓存~";
                                    }
                                    Fluttertoast.showToast(msg: tip);
                                  });
                                  break;
                                // 删除缓存
                                case 6:
                                  MyCache.delete(data[index]);
                                  setState(() {
                                    data.removeAt(index);
                                  });
                                  Fluttertoast.showToast(msg: "删除缓存成功~");
                                  break;
                            }
                          },
                        ),
                        dense: true,
                        onTap: (){
                          play(context,data[index],index);
                        },
                      )
                  );
                }
              },
                  childCount: data.length + 1),
              itemExtent: _item_height)
        ],
      ),
    );
  }

  void setStates(Function call){
    setState(() {
        call();
    });
  }

  // 获取歌单ID
  void getSheetId(){
    if(sheetId == -1){
      dynamic ?obj = ModalRoute.of(context)?.settings.arguments;
      sheetId = int.parse(obj[Constant.KEY_SHEET_ID].toString());
    }
  }

  // 删除歌单
  void dropSheet() async{
    getSheetId();
    bool flag = await context.read<SongSheetProvider>().isOnly(sheetId);
    if(flag){
      Fluttertoast.showToast(msg: "该歌单不能删除噢~");
    }
    else{
      Navigator.pop(context);
      TB_SongSheet_Info.dropSheet(context, sheetId);
    }
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

  // 收藏歌手
  void collectSinger() async{
    if(singer == null){
      dynamic ?obj = ModalRoute.of(context)?.settings.arguments;
      singer = obj[Constant.KEY_DATA];
      TB_Singer.insert(singer!);
    }
  }

  // 播放歌曲
  void play(BuildContext context,MSong song,int index) async{
    context.read<SongQueueProvider>().setQueue(data.sublist(index));
    MyAudio.play(context, song);
  }
}