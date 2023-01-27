import 'package:cached_network_image/cached_network_image.dart';
import 'package:music/common/Constant.dart';
import 'package:music/db/MySP.dart';
import 'package:music/db/tb/TB_Singer.dart';
import 'package:music/db/tb/TB_SongSheet.dart';
import 'package:music/db/tb/TB_SongSheet_Info.dart';
import 'package:music/model/MSinger.dart';
import 'package:music/model/MSongSheet.dart';
import 'package:music/provider/SongSheetProvider.dart';
import 'package:music/router/MyRouter.dart';
import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:music/widget/BaseLayout.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/painting.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/widget/CommonWidget.dart';
import 'package:provider/src/provider.dart';


class Mine extends BaseLayout{

  @override
  BaseLayoutState<BaseLayout> getState() {
    return _MineState();
  }

}

class _MineState extends BaseLayoutState<Mine>{

  final double size_avatar = 80,margin_all = 25,size_title = 20,margin_chip = 15;

  TextEditingController _newSheetTC = TextEditingController();

  bool firstLoaded = false;

  final colors = [
    [Color(0xFFABDCFF), Color(0xFF0396FF)],
    [Color(0xFFFFD3A5), Color(0xFFFD6585)],
    [Color(0xFFFFAA85), Color(0xFFB3315F)],
    [Color(0xFFa18cd1), Color(0xFFfbc2eb)],
    [Color(0xFF84fab0), Color(0xFF8fd3f4)],
    [Color(0xFFe0c3fc), Color(0xFF8ec5fc)],
    [Color(0xFFf5f7fa), Color(0xFFc3cfe2)],
    [Color(0xFFaccbee), Color(0xFFe7f0fd)],
    [Color(0xFF44A08D), Color(0xFF093637)],
    [Color(0xFF43C6AC), Color(0xFF191654)],
    [Color(0xFFFFAFBD), Color(0xFFffc3a0)],
  ];

  final functionBar = [{'key':1,'value':'本地缓存音乐'},
                       {'key':2,'value':'本地音乐'},
                       {'key':3,'value':'歌曲播放列表'},
                       {'key':4,'value':'收藏的歌手'},
                       {'key':5,'value':'我的喜欢'},];

  String userName = "",userDesc = "";

  @override
  Widget getChild(BuildContext context) {
    firstLoad(context);
    var sheet = context.read<SongSheetProvider>().data;
    return LayoutBuilder(builder: (BuildContext context,BoxConstraints viewportConstraints){
        return SingleChildScrollView(
            child: ConstrainedBox(
                constraints: BoxConstraints(
                  minHeight: viewportConstraints.maxHeight
                ),
              child: Container(
                margin: EdgeInsets.all(margin_all),
                child: Column(
                  children: [
                    // 头部
                    Row(
                      children: [
                        ClipRRect(
                            borderRadius: BorderRadius.circular(size_avatar),
                            child:CachedNetworkImage(
                                imageUrl:"https://static.jqwong.cn/202109181358904.png",
                                width: size_avatar,
                                height: size_avatar,
                                fit: BoxFit.cover
                            )),
                        Expanded(
                            child: Container(
                              height: size_avatar - 15,
                              margin: EdgeInsets.only(left: 10,right: 10,top: 15),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(CommonUtil.subString(userName, 6),style: TextStyle(fontSize: 22,fontWeight: FontWeight.bold)),
                                  Text(CommonUtil.subString(userDesc, 8))
                                ],
                              ),
                            )),
                        FlatButton(
                          child: Text("修改资料"),
                          color: Colors.blue,
                          highlightColor: Colors.blue[700],
                          colorBrightness: Brightness.dark,
                          splashColor: Colors.grey,
                          onPressed: (){
                            updateUserInfo(context);
                          },
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                        )
                      ],
                    ),

                    // 标签
                    Container(
                      margin: EdgeInsets.only(top: 30,bottom: 20),
                      child: Row(
                        children: [
                          Expanded(child: Column(
                            children: [
                              CommonUtil.title(text: "${functionBar.length}",size: size_title),
                              Text("功能栏位")
                            ],
                          )),
                          Expanded(child: Column(
                            children: [
                              CommonUtil.title(text: sheet.length.toString(),size: size_title),
                              Text("歌单数量")
                            ],
                          )),
                          Expanded(child: Column(
                            children: [
                              CommonUtil.title(text: "0",size: size_title),
                              Text("未开放")
                            ],
                          ))
                        ],
                      ),
                    ),

                    // 功能栏
                    Container(
                      width: double.infinity,
                      margin: EdgeInsets.only(top: 10),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          CommonUtil.title(text: "功能区域",size: size_title),
                          Wrap(
                            children: getFunctionBarWidget()
                          )
                        ],
                      ),
                    ),

                    // 歌单
                    Container(
                      width: double.infinity,
                      margin: EdgeInsets.only(top: margin_all,bottom: 40),
                      child:
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              Expanded(child: CommonUtil.title(text: "歌单",size: size_title)),
                              GestureDetector(
                                onTap: (){
                                  //_launchURL("https://jqwong.cn");
                                  _newSheetTC.clear();
                                  newSheetDialog(context, _newSheetTC, (v){
                                      newSongSheet(context,v);
                                  });
                                },
                                child: Container(
                                  color: Colors.transparent,
                                  padding: EdgeInsets.all(10.0),
                                  child: CommonUtil.content(text: "新建歌单"),
                                ),
                              )
                            ],
                          ),
                          GridView.builder(
                              itemCount: context.read<SongSheetProvider>().data.length,
                              shrinkWrap: true,
                              physics: NeverScrollableScrollPhysics(),
                              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                                  crossAxisCount: 2,
                                  childAspectRatio: 1.0,
                                  crossAxisSpacing: 15,
                                  mainAxisSpacing: 15
                              ),
                              itemBuilder: (BuildContext context,int index){
                                return GestureDetector(
                                  onTap: (){ Navigator.pushNamed(context, MyRouter.PAGE_SONG_SHEET,arguments: { Constant.KEY_SHEET_ID:sheet[index].id,Constant.KEY_TITLE:sheet[index].name}); },
                                  child: Card(
                                      elevation: 3,
                                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                                      child:
                                      Container(
                                          child: Column(
                                            crossAxisAlignment: CrossAxisAlignment.start,
                                            children: [
                                              Expanded(
                                                  child: Container(
                                                    child: CommonUtil.title(text: sheet[index].name,size: 18),
                                                    margin: EdgeInsets.all(15),
                                                  )),
                                              Container(
                                                margin: EdgeInsets.all(10),
                                                child: Column(
                                                  children: [
                                                    CommonUtil.title(text: sheet[index].nums.toString(),size: 22),
                                                    CommonUtil.content(text: "  首歌曲",size: 12),
                                                  ],
                                                ),
                                              )
                                            ],
                                          ),
                                          decoration: BoxDecoration(
                                              borderRadius: BorderRadius.circular(20),
                                              gradient: LinearGradient(
                                                  colors: colors[index],
                                                  begin: Alignment.topLeft,
                                                  end: Alignment.bottomRight)
                                          )
                                      )
                                  ),
                                );
                              })
                        ])),
                  ],
                ),
              )
            ),
        );
    });
  }

  @override
  void renderFinish() {
    _load();
  }

  @override
  BaseStatus initStatus() {
    return BaseStatus.loading;
  }

  @override
  void initState2() {
    title = "我的";
    _load();
  }

  void _load() async{
    setState(() {
      status = BaseStatus.success;
    });
  }

  // 处理第一次加载
  void firstLoad(BuildContext context) async{
    if(!firstLoaded){
      initSongSheet(context);
      initUserInfo();
      firstLoaded = true;
    }
  }

  // 初始化歌单
  void initSongSheet(BuildContext context) async{
    List<MSongSheet> temp = await TB_SongSheet_Info.get();
    context.read<SongSheetProvider>().initData(temp);
  }

  // 初始化用户信息
  void initUserInfo() async{
    var tempName = await MySP.instance.getString(Constant.SP_NAME),
        tempDesc = await MySP.instance.getString(Constant.SP_DESC);
    tempName = tempName == "" ? "暂未设置" : tempName;
    setState(() {
      userName = tempName;
      userDesc = tempDesc;
    });
  }

  // 新建歌单
  void newSongSheet(BuildContext context,String name) async{
    await TB_SongSheet.create(name, 0);
    initSongSheet(context);
  }

  // 处理功能栏点击事件
  void handleFunctionBar(int key){
    switch(key){
      // 本地缓存音乐
      case 1:
        Navigator.pushNamed(context, MyRouter.PAGE_CACHE);
        break;
      case 2:
        break;
      // 歌曲播放列表
      case 3:
        // todo
        break;
      // 收藏的歌手
      case 4:
        showCollectSinger(context);
        break;
      // 我的喜欢
      case 5:
        Navigator.pushNamed(context, MyRouter.PAGE_SONG_SHEET,arguments: { Constant.KEY_SHEET_ID:1,Constant.KEY_TITLE:"我的喜欢"  });
        break;
    }
  }

  // 修改用户信息
  void updateUserInfo(BuildContext context){
    CommonWidget.updateUserInfo(context, userName, userDesc, (String newName,String newDesc){
      MySP.instance.setString(Constant.SP_NAME, newName);
      MySP.instance.setString(Constant.SP_DESC, newDesc);
      setState(() {
        userName = newName;
        userDesc = newDesc;
      });
    });
  }

  // 展示收藏的歌手
  void showCollectSinger(BuildContext context) async{

    List<MSinger> singers = await TB_Singer.load();

    if(singers.length == 0){
      Fluttertoast.showToast(msg: "暂无收藏的歌手~");
    }
    else{
      CommonWidget.showCollectSingerDialog(context, singers);
    }
  }

  // 功能栏Widget
  List<Widget> getFunctionBarWidget(){
    List<Widget> widgets = [];
    for(var i = 0; i < functionBar.length; i++){
      //print(functionBar[i]['key']);
      widgets.add(Container(
        margin: EdgeInsets.only(top: margin_chip,right: margin_chip),
        child: ActionChip(
          label: CommonUtil.content(text: functionBar[i]['value'],size: 14,color: Colors.black87),
          backgroundColor: Colors.grey.shade300,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5.0)),
          onPressed: (){
            int key = int.parse(functionBar[i]['key'].toString());
            handleFunctionBar(key);
          },
        ),
      ));
    }
    return widgets;
  }

  // 新建歌单歌单Dialog
  newSheetDialog(BuildContext context,TextEditingController tc,Function call) async{
    var dialog = await showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: Text("新建歌单"),
            content: TextField(
                controller: tc,
                decoration: InputDecoration(
                  hintText: "请输入歌单名称",
                  hintStyle: TextStyle(fontSize: 13), // 取消下边框
                  contentPadding: EdgeInsets.all(0),
                )
            ),
            actions: <Widget>[
              FlatButton(
                  child: Text("取消"),
                  onPressed: () => Navigator.pop(context, "cancel")),
              FlatButton(
                  child: Text("确定"),
                  onPressed: (){
                    Navigator.pop(context, "yes");
                    if(tc.value.text != ""){
                      call(tc.value.text);
                    }
                  }),
            ],
          );
        });
    return dialog;
  }

}