import 'package:music/db/MyDB.dart';

class TB_HotSearch{
  static final String TABLENAME = "tb_hot_search",
                      THS_ID    = "ths_id",
                      THS_KEY   = "ths_key",
                      CREATE    = "create table ${TABLENAME}("
                          "${THS_ID} integer primary key autoincrement,"
                          "${THS_KEY} text"
                          ")";

  // 获取
  static Future<List> get() async{
    var db = await MyDB.getInstance();
    String sql = "select * from ${TABLENAME}";
    List<Map> ?list = await db?.rawQuery(sql);
    return toList(list!);
  }

  static void update(List data){
    del();
    insert(data);
  }

  // 删除所有
  static Future<void> del() async{
    MyDB.getDB()?.execute("delete from ${TABLENAME}");
  }

  // 批量添加
  static Future<void> insert(List data) async{
      data.forEach((v) {
          MyDB.getDB()?.rawInsert("insert into ${TABLENAME}(${THS_KEY}) values('${v}')");
      });
  }

  static List<String> toList(List<Map> maps){
    return List.generate(maps.length, (index){
      return maps[index][THS_KEY].toString();
    });
  }
}