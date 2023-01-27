import 'package:music/component/Cache.dart';
import 'package:music/component/LeaderBoardResult.dart';
import 'package:music/component/SearchResult.dart';
import 'package:music/component/Singer.dart';
import 'package:music/component/Song.dart';
import 'package:music/component/SongSheet.dart';
import 'package:flutter/cupertino.dart';

class MyRouter{

  static final MyRouter instance = MyRouter._internal();

  factory MyRouter() => instance;

  MyRouter._internal(){
    // todo
  }

  static final String
      PAGE_LEABOARD_RESULT = "leaderboard_result",
      PAGE_SEARCH_RESULT   = "search_result",
      PAGE_SINGER          = "singer",
      PAGE_SONG            = "song",
      PAGE_SONG_SHEET      = "song_sheet",
      PAGE_CACHE           = "cache",
      PATH = "path",
      VIEW = "view";

  // 路由表
  List<Map<String,dynamic>> routes() {
    return [
      {
        PATH:PAGE_LEABOARD_RESULT,
        VIEW:LeaderBoardResult(),
      },
      {
        PATH:PAGE_SEARCH_RESULT,
        VIEW:SearchResult()
      },
      {
        PATH:PAGE_SINGER,
        VIEW:Singer()
      },
      {
        PATH:PAGE_SONG,
        VIEW:Song()
      },
      {
        PATH:PAGE_SONG_SHEET,
        VIEW:SongSheet()
      },
      {
        PATH:PAGE_CACHE,
        VIEW:Cache()
      }
    ];
  }

  // 遍历
  Map<String,Widget Function(BuildContext)> getRoutes(BuildContext context){
    Map<String,Widget Function(BuildContext)> router = {};
    for(var route in routes()){
      router[route[PATH]] = (context)=>route[VIEW];
    }
    return router;
  }
}