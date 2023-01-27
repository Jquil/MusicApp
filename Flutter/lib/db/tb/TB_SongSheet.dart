import 'package:flutter/cupertino.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/db/MyDB.dart';
import 'package:music/db/tb/TB_SongSheet_Info.dart';
import 'package:music/model/MSong.dart';
import 'package:music/provider/SongSheetProvider.dart';
import 'package:provider/src/provider.dart';
import 'package:sqflite/sqflite.dart';

class TB_SongSheet{
  static final TABLENAME_PREFIX  = "tb_songsheet_",
                          TSS_ID = "tss_id",
                          TSS_MUSICRID = "tss_musicRid",
                          TSS_NAME     = "tss_name",
                          TSS_PIC      = "tss_pic",
                          TSS_ARTIST   = "tss_artist",
                          TSS_ARTISTID = "tss_artistId",
                          TSS_ALBUM    = "tss_album",
                          TSS_ALBUMPIC = "tss_albumpic",
                          TSS_SONGTIMEMINUTES = "tss_songTimeMinutes",
                          TSS_HASMV    = "tss_hasmv";

  static Future<int> create(String name,int only) async{
    var db = await MyDB.getInstance();
    int newId = await TB_SongSheet_Info.insert(name, only);
    //print("newId = ${newId}");
    db?.execute(createSQL(newId));
    return newId;
  }

  static void collect(BuildContext context,int sheetId,MSong song) async{
    insert(sheetId, song,(exit){
      if(exit){
        Fluttertoast.showToast(msg: "'${song.name}'已存在");
      }
      else{
        Fluttertoast.showToast(msg: "收藏成功~");
        context.read<SongSheetProvider>().changeNums(sheetId, true);
      }
    });
  }

  static void remove(BuildContext context,int sheetId,MSong song) async{
    context.read<SongSheetProvider>().changeNums(sheetId, false);
    delete(sheetId, song);
    Fluttertoast.showToast(msg: "已移出~");
  }

  static void insert(int sheetId,MSong song,Function exit) async{
      bool flag = await isExit(sheetId,song.musicrid);
      //print(flag);

      if(!flag){
        Database ?db = await MyDB.getInstance();
        db?.insert("${TABLENAME_PREFIX}${sheetId}", song.toMap());
      }
      exit(flag);
  }
  
  static void delete(int sheetId,MSong song) async{
    Database? db = await MyDB.getDB();
    db?.execute("delete from ${TABLENAME_PREFIX}${sheetId} where ${TSS_MUSICRID} = '${song.musicrid}'");
  }

  static Future<bool> isExit(int sheetId,String musicrid) async{
    Database ?db = await MyDB.getInstance();
    var temp = await db?.rawQuery("select * from ${TABLENAME_PREFIX}${sheetId} where ${TSS_MUSICRID} = '${musicrid}'");
    //print(temp);
    if(temp!.length > 0){
      return true;
    }
    else{
      return false;
    }
  }

  static Future<List<MSong>> load(int sheetId,int page,int itemSize) async{
    List<MSong> data = [];
    Database ?db = await MyDB.getInstance();
    var query = await db?.rawQuery("select * from ${TABLENAME_PREFIX}${sheetId} order by ${TSS_ID} desc limit ${itemSize} offset ${ (page - 1) * itemSize  }");
    for(int i = 0; i < query!.length; i++ ){
      // pic120 => pic
      data.add(MSong(query[i][TSS_MUSICRID].toString(), query[i][TSS_ARTIST].toString(), query[i][TSS_PIC].toString(), query[i][TSS_ALBUM].toString(), int.parse(query[i][TSS_ARTISTID].toString()), query[i][TSS_ALBUMPIC].toString(), query[i][TSS_SONGTIMEMINUTES].toString(), query[i][TSS_PIC].toString(), query[i][TSS_NAME].toString()));
    }
    return data;
  }

  static String createSQL(int songId){
    return "create table ${TABLENAME_PREFIX}${songId}("
        "${TSS_ID} integer primary key autoincrement,"
        "${TSS_NAME} text,"
        "${TSS_PIC} text,"
        "${TSS_MUSICRID} text,"
        "${TSS_ARTIST} text,"
        "${TSS_ARTISTID} integer,"
        "${TSS_ALBUM} text,"
        "${TSS_ALBUMPIC} text,"
        "${TSS_SONGTIMEMINUTES} text,"
        "${TSS_HASMV} integer"
        ")";
  }
}