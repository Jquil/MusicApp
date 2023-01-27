import 'dart:async';
import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:music/api/ApiService.dart';
import 'package:music/db/tb/TB_Cache_Song.dart';
import 'package:music/http/MyDio.dart';
import 'package:music/model/MCacheFile.dart';
import 'package:music/model/MSong.dart';
import 'package:path_provider/path_provider.dart';

class MyCache{

  static List<String> format = [".mp3",".aac","wav"];

  static Future<void> isExit(String musicrid) async{
    // database -> filesys
  }

  // 缓存
  static Future<void> cache1(MSong song,Function call) async{
    String url = await ApiService.instance.getPlayUrl(song.musicrid);
    String filename = "${song.musicrid}${url.substring(url.lastIndexOf("."))}";
    Directory tempDir = await getTemporaryDirectory();
    String path = "${tempDir.path}${Platform.pathSeparator}${filename}";
    var file = File(path);
    if(file.existsSync()){
        call(false);
    }
    else{
      TB_Cache_Song.insert(song);
      call(true);
      file.create(recursive: true);
      Response res = await MyDio.dio.get(url,options: MyDio.option1);
      Uint8List bytes = res.data;
      file.writeAsBytes(bytes);
    }
  }

  // 获取所有缓存
  static Future<List<MCacheFile>> get() async{
    Directory tempDir = await getTemporaryDirectory();
    Stream<FileSystemEntity> filelist = Directory(tempDir.path).list();
    List<MCacheFile> data = [];
    await for(FileSystemEntity fse in filelist){
      if(fse.path.contains(".mp3")){
        data.add(MCacheFile(fse.path.substring(fse.path.lastIndexOf("/") + 1), fse.path));
      }
    }
    return data;
  }

  // 同步缓存文件与数据库
  static Future<List<MSong>> sync() async{
    List<MSong> data = await TB_Cache_Song.get();
    //print("data length = ${data.length}");
    List<MCacheFile> filelist = await get();
    //print("filelist length = ${filelist.length}");
    var index = [];

    for(int i = 0; i < data.length; i++){
      bool flag = false;
      for(int j = 0; j < filelist.length; j++){
        //print(filelist[j].name.substring(0,filelist[j].name.lastIndexOf(".")));
        if(filelist[j].name.substring(0,filelist[j].name.lastIndexOf(".")) == data[i].musicrid){
          flag = true;
          break;
        }
      }

      if(!flag){
        //print("need to del = ${data[i].musicrid}");
        index.add(i);
        TB_Cache_Song.delete1(data[i].musicrid);
      }
    }
    //print("remove length = ${index.length}");
    for(int i = 0; i < index.length; i++){
      data.removeAt(index[i]);
    }
    return data;
  }

  // 删除缓存
  static void delete(MSong song) async{
    TB_Cache_Song.delete1(song.musicrid);
    String path = await getCachePath(song.musicrid);
    File file = File(path);
    if(file.existsSync()){
      file.delete();
    }
  }

  // 获取缓存路径
  static Future<String> getCachePath(String musicrid) async{
    Directory tempDir = await getTemporaryDirectory();
    Stream<FileSystemEntity> filelist = Directory(tempDir.path).list();
    String path = "";
    await for(FileSystemEntity item in filelist){
      if(item.path.contains(musicrid)){
        path = item.path;
        break;
      }
    }
    return path;
  }
}