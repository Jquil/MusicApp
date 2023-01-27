class TB_Collect_Singer{
  static final TABLENAME = "tb_collect_singer",
            TCS_ID       = "tcs_id",
            TCS_ARTISTID = "tcs_artistId",
            TCS_ARTIST   = "tcs_artist",
            TCS_PIC      = "tcs_pic",
            CREATE       = "create table ${TABLENAME}("
                "${TCS_ID} integer primary key autoincrement,"
                "${TCS_ARTISTID} integer,"
                "${TCS_ARTIST} text,"
                "${TCS_PIC} text"
                ")";

  static void collect() async{

  }


}