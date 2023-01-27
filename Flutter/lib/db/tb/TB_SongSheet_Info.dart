import 'package:flutter/cupertino.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/db/MyDB.dart';
import 'package:music/db/tb/TB_SongSheet.dart';
import 'package:music/model/MSongSheet.dart';
import 'package:music/provider/SongSheetProvider.dart';
import 'package:provider/src/provider.dart';
import 'package:sqflite/sqflite.dart';

class TB_SongSheet_Info{
  static final String TABLENAME = "tb_songsheet_info",
                      TSI_ID   = "tsi_id",
                      TSI_NAME = "tsi_name",
                      TSI_ONLY = "tsi_only",
                      CREATE = "create table ${TABLENAME}("
                          "${TSI_ID} integer primary key autoincrement,"
                          "${TSI_NAME} text,"
                          "${TSI_ONLY} integer"
                          ")";


  static Future<int> insert(String name,int only) async{
      var db = await MyDB.getInstance();
      db?.rawInsert("insert into ${TABLENAME}(${TSI_NAME},${TSI_ONLY}) values('${name}',${only})");
      var obj = await db?.rawQuery("select last_insert_rowid() from ${TABLENAME}");
      return int.parse(obj!.first['last_insert_rowid()'].toString());
  }

  static void update(int sheetId,String sheetName) async{
    Database? db = await MyDB.getDB();
    db?.execute("update ${TABLENAME} set ${TSI_NAME} = '${sheetName}' where ${TSI_ID} = ${sheetId}");
  }


  static Future<List<MSongSheet>> get() async{
    List<MSongSheet> data = [];
    var db = await MyDB.getInstance();
    var query = await db?.rawQuery("select * from ${TABLENAME}");

    int length = query == null ? 0 : query.length;
    for(int i = 0; i < length; i++){
      var obj1 = query![i];
      var temp = await db?.rawQuery("select count(*) from ${TB_SongSheet.TABLENAME_PREFIX}${obj1[TSI_ID]}");
      var obj2 = temp![0];
      data.add(MSongSheet(int.parse(obj1[TSI_ID].toString()),int.parse(obj1[TSI_ONLY].toString()), int.parse(obj2["count(*)"].toString()), obj1[TSI_NAME].toString()));
    }
    return data;
  }

  static void dropSheet(BuildContext context,int sheetId) async{
    Database? db = MyDB.getDB();
    db?.execute("drop table ${TB_SongSheet.TABLENAME_PREFIX}${sheetId}");
    db?.execute("delete from ${TABLENAME} where ${TSI_ID} = ${sheetId}");
    context.read<SongSheetProvider>().deleteSheet(sheetId);
    Fluttertoast.showToast(msg: "删除歌单成功~");
  }
}