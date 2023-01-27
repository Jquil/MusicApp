import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/model/MSinger.dart';
import 'package:sqflite/sqflite.dart';

import '../MyDB.dart';

class TB_Singer{

  static final String TABLENAME = "tb_singer",
                      TS_ID = "ts_id",
                      TS_PIC = "ts_pic",
                      TS_GENER = "ts_gener",
                      TS_NAME = "name",
                      TS_ARTISTID = "ts_artistid",
                      TS_COUNTRY = "ts_country",
                      CREATE = "create table ${TABLENAME}("
                          "${TS_ID} integer primary key autoincrement,"
                          "${TS_PIC} text,"
                          "${TS_GENER} text,"
                          "${TS_NAME} text,"
                          "${TS_ARTISTID} integer,"
                          "${TS_COUNTRY} text"
                          ")";

  static void insert(MSinger singer) async{
    bool flag = await isExit(singer.id);
    if(flag){
      Fluttertoast.showToast(msg: "歌手'${singer.name}'已收藏~");
    }
    else{
      Database ?db = await MyDB.getInstance();
      db?.insert("${TABLENAME}", singer.toMap());
      Fluttertoast.showToast(msg: "收藏歌手成功~");
    }
  }

  static void delete(int artistid) async{
    Database? db = await MyDB.getDB();
    db?.execute("delete from ${TABLENAME} where ${TS_ARTISTID} = ${artistid}}");
    Fluttertoast.showToast(msg: "取消收藏成功~");
  }

  static Future<bool> isExit(int artistid) async{
    Database ?db = await MyDB.getInstance();
    var temp = await db?.rawQuery("select * from ${TABLENAME} where ${TS_ARTISTID} = ${artistid}");
    //print(temp);
    if(temp!.length > 0){
      return true;
    }
    else{
      return false;
    }
  }

  static Future<List<MSinger>> load() async{
    List<MSinger> data = [];
    Database ?db = await MyDB.getInstance();
    var query = await db?.rawQuery("select * from ${TABLENAME} order by ${TS_ID} desc");
    for(int i = 0; i < query!.length; i++ ){
      // pic120 => pic
      data.add(MSinger(query[i][TS_PIC].toString(), query[i][TS_GENER].toString(), query[i][TS_NAME].toString(), int.parse(query[i][TS_ARTISTID].toString()), query[i][TS_COUNTRY].toString()));
    }
    return data;
  }
}