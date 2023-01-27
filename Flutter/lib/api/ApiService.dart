import 'dart:convert';
import 'package:music/api/Api.dart';
import 'package:music/common/Constant.dart';
import 'package:music/http/MyDio.dart';
import 'package:music/model/MLeaderBoard.dart';
import 'package:music/model/MLeaderBoardResult.dart';
import 'package:music/model/MLrcList.dart';
import 'package:music/model/MSinger.dart';
import 'package:music/model/MSong.dart';
import 'package:dio/dio.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/model/MVersion.dart';


class ApiService {

  static final ApiService instance = ApiService._internal();

  factory ApiService() => instance;

  ApiService._internal(){
    // todo
  }

  // 获取排行榜
  Future<List<MLeaderBoard>> getLeaderBoard() async {
    List<MLeaderBoard> data = [];
    try {
      Response res = await MyDio.dio.get(Api.LEADERBOARD);
      List list = json.decode(res.toString())['data'];
      data = list.map((e) => MLeaderBoard.fromJson((e as Map<String, dynamic>))).toList();
    }
    catch (e) {
      // toastr
    }
    return data;
  }

  // 获取排行榜下的歌曲
  Future<MLeaderBoardResult> getLeaderBoardResult(int sourceid,int page,int itemSize) async{
    try{
      Response res = await MyDio.dio.get(Api.getLeaderBoardResult(sourceid, page, itemSize));

      Map m = json.decode(res.toString())['data'];
      return MLeaderBoardResult.fromJson(m as Map<String,dynamic>);
    }
    catch(e){
      Fluttertoast.showToast(msg: e.toString());
      print(e.toString());

    }
    return MLeaderBoardResult("", []);
  }

  // 获取热搜
  Future<List> getHorSearch() async{
    List data = [];
    try{
      Response res = await MyDio.dio.get(Api.HOTSEARCH);
      data.addAll(json.decode(res.toString())['data']);
    }
    catch(e){
      // todo
    }
    return data;
  }

  // 搜索
  Future<List<MSong>> search(String key,int page,int itemSize) async{
    List<MSong> data = [];
    try{
      Response res = await MyDio.dio.get(Api.getSearch(key, page, itemSize));
      List list = json.decode(res.toString())['data']['list'];
      for(int i = 0; i < list.length; i++){
        data.add(MSong.fromJson(list[i]));
      }
    }
    catch(e){
      print(e.toString());
    }
    return data;
  }

  // 获取歌手下的歌曲
  Future<List<MSong>> getSongBySinger(int artistid,int page,int itemSize) async{
    List<MSong> data = [];
    try{
      Response res = await MyDio.dio.get(Api.getSongBySinger(artistid, page, itemSize));
      List list = json.decode(res.toString())['data']['list'];
      print(list.length);
      for(int i = 0; i < list.length; i++){
        data.add(MSong.fromJson(list[i]));
      }
    }
    catch(e){
      print(e.toString());
    }
    return data;
  }

  // 获取播放地址
  Future<String> getPlayUrl(String musicrid) async{
    try{
      Response res = await MyDio.dio.get(Api.getPlayUrl(musicrid));
      return res.data;
    }
    catch(e){
      Fluttertoast.showToast(msg: e.toString());
    }
    return "";
  }

  // 获取歌词
  Future<MLrcList> getLyric(String musicrid) async{
      MLrcList list = MLrcList([]);
      try{
        Response res = await MyDio.dio.get(Api.getLrcList(musicrid));
        Map m = json.decode(res.toString())['data'];
        list = MLrcList.fromJson(m as Map<String,dynamic>);
      }
      catch(e){
        Fluttertoast.showToast(msg: e.toString());
        print(e.toString());
      }
      return list;
  }

  // 获取最新版本
  Future<MVersion> getNewestVersion() async{
    // https://jqwong.cn/api/music/version.html
    MVersion version = MVersion("", "", "", "", []);
    try{
      Response res = await MyDio.dio.get(Constant.O_VERSION);
      Map map = json.decode(res.toString());
      version = MVersion(map['version'], map['title'], map['apk'], map['ipa'], map['content']);
    }
    catch(e){
      // todo
    }
    return version;
  }

  // 获取歌手信息
  Future<MSinger?> getSingerInfo(int artistid) async{
    MSinger? singer;
    try{
      Response res = await MyDio.dio.get(Api.getSingerInfo(artistid));
      Map m = json.decode(res.toString())['data'];
      singer = MSinger.fromJson(m as Map<String,dynamic>);
    }
    catch(e){
      Fluttertoast.showToast(msg: e.toString());
    }
    return singer;
  }
}