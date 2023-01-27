import 'package:music/common/Constant.dart';

import '../MyDB.dart';

class TB_Search_History{
  static final String TABLENAME  = "tb_search_history",
                      TSH_ID  = "ths_id",
                      TSH_KEY = "ths_key",
                      CREATE  = "create table ${TABLENAME}("
                          "${TSH_ID} integer primary key autoincrement,"
                          "${TSH_KEY} text"
                          ")";

  // 添加
  static Future<void> insert(String key) async{
    MyDB.getDB()?.rawInsert("insert into ${TABLENAME}(${TSH_KEY}) values('${key}')");
  }

  // 获取
  static Future<List> get() async{
    var db = await MyDB.getInstance();
    String sql = "select * from ${TABLENAME} order by ${TSH_ID} desc limit ${Constant.HISTORY_ITEM_SIZE}";
    List<Map> ?list = await db?.rawQuery(sql);
    return toList(list!);
  }

  static List<String> toList(List<Map> maps){
    return List.generate(maps.length, (index){
      return maps[index][TSH_KEY].toString();
    });
  }
}