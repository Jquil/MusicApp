import 'dart:math';

import 'package:music/db/MySP.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class CommonUtil{

  // 截取文字
  static String subString(String value,int maxSize){
    return value.length > maxSize ? value.substring(0,maxSize) + "..." : value;
  }

  // Text - title
  static Text title({ @required text,double size = 16,color}){
    return Text(text,style: TextStyle(fontSize: size,fontWeight: FontWeight.bold,color: color));
  }

  // Text - content
  static Text content({ @required text,double size = 16,color}){
    return Text(text,style: TextStyle(fontSize: size,color: color));
  }

  // 随机数
  static int randomInt(size){
    return Random().nextInt(size);
  }

  // 背景1
  static Color getStateLayoutBG(BuildContext context){
    return Theme.of(context).brightness == Brightness.dark ? Colors.black : Colors.white;
  }

  // 背景2
  static Color getStateLayoutBG2(BuildContext context){
    return Theme.of(context).brightness == Brightness.dark ? Colors.white : Colors.black;
  }

  // 日期是否一致
  static Future<bool> isDateFit(String key) async{
    var date1 = getDate();
    var date2 = await MySP.instance.getString(key);
    return date1 == date2;
  }

  // 获取日期
  static String getDate(){
    var dt = DateTime.now();
    return "${dt.year}-${dt.month}-${dt.day}";
  }

  // pic120 to 700
  static String pic120to700(String pic120){
    //print(pic120.replaceAll("/120/", "/700/"));
    return pic120.replaceAll("/120/", "/700/");
  }


}