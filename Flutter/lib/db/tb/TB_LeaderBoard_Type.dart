import 'package:music/db/MyDB.dart';
import 'package:music/db/tb/TB_LeaderBoard.dart';
import 'package:music/model/MLeaderBoard.dart';

class TB_LeaderBoard_Type{
  static final String  TABLENAME = "tb_leaderboard_type",
                        TLT_ID   = "tlt_id",
                        TLT_NAME = "tlt_name",
                        CREATE   = "create table ${TABLENAME}("
                            "${TLT_ID} integer primary key autoincrement,"
                            "${TLT_NAME} text"
                            ")";

  static Future<void> update(List<MLeaderBoard> data) async{
    await del();
    await TB_LeaderBoard.del();
    insert(data);
  }

  static Future<void> insert(List<MLeaderBoard> data) async{
    var db = await MyDB.getInstance();
    data.forEach((el) {
      MyDB.getDB()?.rawInsert("insert into ${TABLENAME}(${TLT_NAME}) values('${el.name}')");
      var temp = db?.rawQuery("select last_insert_rowid() from ${TABLENAME}");
      temp?.then((value){
        int id = int.parse(value[0]['last_insert_rowid()'].toString());
        TB_LeaderBoard.insert(el.list,id);
      });
    });
  }

  static Future<void> del() async{
    var db = await MyDB.getInstance();
    db?.rawQuery("delete from ${TABLENAME}");
  }

  static Future<List<MLeaderBoard>> get() async{
    var db = await MyDB.getInstance();
    List<MLeaderBoard> data = [];
    var temp = await db?.rawQuery("select * from ${TABLENAME}");

    int length = temp == null ? 0 : temp.length;
    for(int i  = 0; i < length; i++){
      var obj  = temp?[i];
      var id   = obj?[TLT_ID].toString();
      var name = obj?[TLT_NAME].toString();
      var list = await TB_LeaderBoard.get(int.parse(id.toString()));
      data.add(MLeaderBoard(name!, list));
    }
    return data;
  }


  static void toList(){

  }
}