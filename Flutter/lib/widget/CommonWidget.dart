import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:music/common/Constant.dart';
import 'package:music/model/MSinger.dart';
import 'package:music/model/MSongSheet.dart';
import 'package:music/provider/SongSheetProvider.dart';
import 'package:music/router/MyRouter.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:provider/src/provider.dart';

class CommonWidget{

  static showSongSheetDialog(BuildContext context,Function call) async{
    showModalBottomSheet(
        context: context,
        builder: (context){
          return Container(
            width: double.infinity,
            height: 200,
            child: songSheetDialog(context.read<SongSheetProvider>().data, (int sheetId){
              call(sheetId);
              Navigator.pop(context);
            }),
          );
        }
    );
  }

  static Widget songSheetDialog(List<MSongSheet> sheet,Function call){
    return ListView.builder(
      itemBuilder: (BuildContext context,int index){
        double left = (-16 + (((index+1).toString().length) * 5));
        return ListTile(
          leading: Text(" ${(index+1).toString()}",style: TextStyle(fontSize: 20,fontStyle: FontStyle.italic)),
          title: Transform(
                  transform: Matrix4.translationValues(left, 0.0, 0.0),
                  child:CommonUtil.title(text: sheet[index].name)),
          onTap: (){
            call(sheet[index].id);
          }
        );
      },
      itemCount: sheet.length,
    );
  }

  static updateSheetDialog(BuildContext context,TextEditingController tc,String sheetName,Function call) async{
    var dialog = await showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: Text("修改歌单"),
            content: TextField(
                controller: tc,
                decoration: InputDecoration(
                  hintText: sheetName,
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
                    if(tc.value.text != "" && tc.value.text != sheetName){
                      call(tc.value.text);
                    }
                  }),
            ],
          );
        });
    return dialog;
  }

  static updateUserInfo(BuildContext context,String name,String desc,Function call) async{
    TextEditingController tc1 = TextEditingController(),tc2 = TextEditingController();
    var dialog = await showDialog(
        context: context,
        builder: (BuildContext context){
          return AlertDialog(
            title: Text("修改用户信息"),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                    controller: tc1,
                    decoration: InputDecoration(
                      hintText: name,
                      hintStyle: TextStyle(fontSize: 13), // 取消下边框
                      contentPadding: EdgeInsets.all(0),
                    )
                ),
                TextField(
                    controller: tc2,
                    decoration: InputDecoration(
                      hintText: desc,
                      hintStyle: TextStyle(fontSize: 13), // 取消下边框
                      contentPadding: EdgeInsets.all(0),
                    )
                ),
              ],
            ),
            actions: <Widget>[
              FlatButton(
                  child: Text("取消"),
                  onPressed: () => Navigator.pop(context, "cancel")),
              FlatButton(
                  child: Text("确定"),
                  onPressed: (){
                    Navigator.pop(context, "yes");
                    //if(tc1.value.text != "" && tc2.value.text != ""){
                      call(tc1.value.text,tc2.value.text);
                    //}
                  }),
            ],
          );
        });
    return dialog;
  }

  static showCollectSingerDialog(BuildContext context,List<MSinger> data) async{
    showModalBottomSheet(
        context: context,
        builder: (context){
          return Container(
            width: double.infinity,
            height: 200,
            child: ListView.builder(
                itemBuilder: (BuildContext context,int index){
                  return GestureDetector(
                    child: Container(
                      width: double.infinity,
                      color: Colors.transparent,
                      margin: EdgeInsets.all(10),
                      child: Row(
                        children: [
                          ClipRRect(
                              borderRadius: BorderRadius.circular(45),
                              child:Image.network(data[index].pic,width: 45,height: 45,fit: BoxFit.cover,)),
                          Text("   "),
                          CommonUtil.content(text: CommonUtil.subString(data[index].name, 12))
                        ],
                      ),
                    ),
                    onTap: (){
                      Navigator.pushNamed(context, MyRouter.PAGE_SINGER,arguments: { Constant.KEY_SINGER:data[index].name,Constant.KEY_PIC:data[index].pic.replaceAll("/120/", "/700/"),Constant.KEY_DATA:data[index],Constant.KEY_ARTISTID:data[index].id });
                    },
                    onLongPress: (){
                      // delete
                    },
                  );
                },
                itemCount: data.length,
            ),
          );
        }
    );
  }
}