import 'package:music/model/MSong.dart';
import 'package:sqflite/sqflite.dart';

import '../MyDB.dart';

class TB_Cache_Song{

  /**
   * 文件命名：music_rid.format
   * 通过MUSIC_RID寻找缓存
   * */

  static final String TABLENAME = "tb_cache_song",
                      TCS_ID     = "tcs_id",
                      TCS_MUSICRID = "tcs_musicRid",
                      TCS_NAME     = "tcs_name",
                      TCS_PIC      = "tcs_pic",
                      TCS_ARTIST   = "tcs_artist",
                      TCS_ARTISTID = "tcs_artistId",
                      TCS_ALBUM    = "tcs_album",
                      TCS_ALBUMPIC = "tcs_albumpic",
                      TCS_SONGTIMEMINUTES = "tcs_songTimeMinutes",
                      TCS_HASMV    = "tcs_hasmv",
                      CREATE       = "create table ${TABLENAME}("
                         "${TCS_ID} integer primary key autoincrement,"
                         "${TCS_NAME} text,"
                         "${TCS_PIC} text,"
                         "${TCS_MUSICRID} text,"
                         "${TCS_ARTIST} text,"
                         "${TCS_ARTISTID} integer,"
                         "${TCS_ALBUM} text,"
                         "${TCS_ALBUMPIC} text,"
                         "${TCS_SONGTIMEMINUTES} text,"
                         "${TCS_HASMV} integer"
                         ")";



  static Future<List<MSong>> get() async{
    List<MSong> data = [];
    Database? db = MyDB.getDB();
    var query = await db?.query("${TABLENAME}",orderBy: "${TB_Cache_Song.TCS_ID} desc");
    for(int i = 0; i < query!.length; i++){
      data.add(MSong(query[i][TCS_MUSICRID].toString(), query[i][TCS_ARTIST].toString(), query[i][TCS_PIC].toString(), query[i][TCS_ALBUM].toString(), int.parse(query[i][TCS_ARTISTID].toString()), query[i][TCS_ALBUMPIC].toString(), query[i][TCS_SONGTIMEMINUTES].toString(), query[i][TCS_PIC].toString(), query[i][TCS_NAME].toString()));
    }
    return data;
  }

  static void insert(MSong song) async{
    Database ?db = await MyDB.getInstance();
    db?.insert("${TABLENAME}", song.toMap2());
  }

  static void delete1(String musricrid) async{
    var db = await MyDB.getDB();
    db?.execute("delete from ${TABLENAME} where ${TCS_MUSICRID} = '${musricrid}'");
  }

  static void delete2(List<String> musricrid) async{

  }
}