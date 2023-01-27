import 'package:music/db/tb/TB_Cache_Song.dart';
import 'package:music/db/tb/TB_Collect_Singer.dart';
import 'package:music/db/tb/TB_HotSearch.dart';
import 'package:music/db/tb/TB_LeaderBoard.dart';
import 'package:music/db/tb/TB_LeaderBoard_Type.dart';
import 'package:music/db/tb/TB_Search_History.dart';
import 'package:music/db/tb/TB_Singer.dart';
import 'package:music/db/tb/TB_SongSheet.dart';
import 'package:music/db/tb/TB_SongSheet_Info.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

class MyDB{
  static final MyDB instance = MyDB._internal();

  factory MyDB() => instance;

  static Database ?_db;

  MyDB._internal(){
    init();
  }

  void init() async{
    _db = await _open();
  }

  Future<Database> _open() async{
    return openDatabase(
        join(await getDatabasesPath(),'music.db'),
        onCreate: _onCreate,
        onUpgrade: _onUpgrade,
        version: 3
    );
  }

  void _onCreate(Database db,int version) async{
    var batch = db.batch();
    batch.execute(TB_Search_History.CREATE);
    batch.execute(TB_HotSearch.CREATE);
    batch.execute(TB_Collect_Singer.CREATE);
    batch.execute(TB_LeaderBoard_Type.CREATE);
    batch.execute(TB_LeaderBoard.CREATE);
    batch.execute(TB_SongSheet_Info.CREATE);
    batch.execute("insert into ${TB_SongSheet_Info.TABLENAME}(${TB_SongSheet_Info.TSI_NAME},${TB_SongSheet_Info.TSI_ONLY}) values('我的喜欢',1)");
    batch.execute(TB_SongSheet.createSQL(1));
    batch.execute(TB_Cache_Song.CREATE);
    batch.execute(TB_Singer.CREATE);
    batch.commit();
  }

  void _onUpgrade(Database db,int oldVersion,int newVersion) async{
    print("DB:onUpgrade");
    var batch = db.batch();
    switch(oldVersion){
      case 1:
        batch.execute(TB_Cache_Song.CREATE);
        break;
      case 2:
        batch.execute(TB_Singer.CREATE);
        break;
    }
    batch.commit();
  }

  static Database? getDB(){
    return _db;
  }

  static Future<Database>? getInstance() async{
    if(_db == null){
      await MyDB()._open();
    }
    return _db!;
  }

}