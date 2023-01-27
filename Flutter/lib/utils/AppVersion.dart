import 'dart:io';
import 'package:flutter/material.dart';
import 'package:music/api/ApiService.dart';
import 'package:music/model/MVersion.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:package_info/package_info.dart';
import 'package:url_launcher/url_launcher.dart';

class AppVersion{

  // 1
  static Future<void> checkVersion(Function call) async{
    bool flag = false;
    PackageInfo info = await PackageInfo.fromPlatform();
    int now_version = int.parse(info.version.replaceAll(".", ""));
    MVersion newestInfo = await ApiService.instance.getNewestVersion();
    //print(newestInfo.version);
    int newest_version = int.parse(newestInfo.version.replaceAll(".", ""));
    if(now_version < newest_version){
      flag = true;
    }
    call(flag,newestInfo);
  }

  // 2
  static void showUpdateDialog(BuildContext context,MVersion version,Function call) async{
    showDialog(
        context: context,
        builder: (context){
          return AlertDialog(
            title: Text(version.title),
            content: getUpdateWidget(version),
            actions: <Widget>[
              FlatButton(
                  child: Text("取消"),
                  onPressed: () => Navigator.pop(context, "cancel")),
              FlatButton(
                  child: Text("更新 "),
                  onPressed: (){
                    call();
                    Navigator.pop(context, "cancel");
                  }),
            ],
          );
        }
    );
  }

  // 3
  static Future<void> check(BuildContext context) async{
    checkVersion((bool flag,MVersion version){
      if(flag){
        if(Platform.isAndroid){
          showUpdateDialog(context,version,(){
            androidUpdate(version);
          });
        }
        else if(Platform.isIOS){

        }
      }
    });
  }

  // android更新
  static void androidUpdate(MVersion version) async{
    // 打开浏览器下载更新
    var url = version.apk;
    await canLaunch(url) ? await launch(url) : throw 'Could not launch $url';
  }


  // ios更新
  static void iosUpdate(){

  }


  // widget
  static Widget getUpdateWidget(MVersion version){
    List<Widget> content = [];
    for(var i = 0; i < version.content.length; i++){
      content.add(CommonUtil.content(text: version.content[i].toString()));
    }
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: content,
    );
  }
}