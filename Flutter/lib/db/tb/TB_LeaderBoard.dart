import 'package:music/db/MyDB.dart';
import 'package:music/model/MLeaderBoardItem.dart';

class TB_LeaderBoard{
  static final String TABLENAME = "tb_leaderboard",
                    TL_ID       = "tl_id",
                    TL_SOURCEID = "tl_sourceId",
                    TL_PIC      = "tl_pic",
                    TL_NAME     = "tl_name",
                    TL_INTRO    = "tl_intro",
                    TL_TYPE     = "tl_type",
                    CREATE      = "create table ${TABLENAME}("
                        "${TL_ID} integer primary key autoincrement,"
                        "${TL_SOURCEID} text,"
                        "${TL_PIC} text,"
                        "${TL_NAME} text,"
                        "${TL_INTRO} text,"
                        "${TL_TYPE} integer"
                        ")";


  static void insert(List<MLeaderBoardItem> data, int type) async{
      var db = await MyDB.getInstance();
      data.forEach((el) {
        db?.insert(TABLENAME, el.toMap(type));
      });
  }

  static Future<void> del() async{
    var db = await MyDB.getInstance();
    db?.rawQuery("delete from ${TABLENAME}");
  }

  static Future<List<MLeaderBoardItem>> get(int typeId) async{
    var db = await MyDB.getInstance();
    List<MLeaderBoardItem> data = [];
    var query = await db?.rawQuery("select * from ${TABLENAME} where ${TL_TYPE} = ${typeId}");

    query?.forEach((el) {
      data.add(MLeaderBoardItem(el[TL_SOURCEID].toString(), el[TL_INTRO].toString(), el[TL_NAME].toString(), el[TL_ID].toString(), "", el[TL_PIC].toString(), ""));
    });
    return data;
  }

  void toList(){

  }
}