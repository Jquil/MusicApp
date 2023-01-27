class Api{

  static final ROOT = "https://www.kuwo.cn/api/www",
      LEADERBOARD = "${ROOT}//bang/bang/bangMenu?httpsStatus=1&reqId=e617e730-e1f7-11eb-942d-33e288737b1d",
      HOTSEARCH   = "${ROOT}/search/searchKey?key=&httpsStatus=1&reqId=db3f8670-e1f6-11eb-942d-33e288737b1d";

  static String getSearch(String key,int page,int itemSize){
    return "${ROOT}/search/searchMusicBykeyWord?key=${key}&pn=${page}&rn=${itemSize}&httpsStatus=1&reqId=23016430-e1eb-11eb-a2ee-bf024dbfa4c7";
  }

  static String getLeaderBoardResult(int sourceid,int page,int itemSize){
    return "${ROOT}/bang/bang/musicList?bangId=${sourceid}&pn=${page}&rn=${itemSize}&httpsStatus=1&reqId=b092ca30-e152-11eb-90cc-79484e9fbe4d";
  }

  static String getPlayUrl(String musicrid){
    return "http://antiserver.kuwo.cn/anti.s?type=convert_url&rid=${musicrid}&format=mp3&response=url";
  }

  static String getSingerInfo(int artistid){
    return "${ROOT}/artist/artist?artistid=${artistid}&httpsStatus=1&reqId=b06e62f0-f582-11eb-bd8d-c19fac490f25";
  }

  static String getSongBySinger(int artistid,int page,int itemSize){
    return "${ROOT}/artist/artistMusic?artistid=${artistid}&pn=${page}&rn=${itemSize}&httpsStatus=1&reqId=87263830-f72d-11eb-979c-c11891b4f2ba";
  }

  static String getLrcList(String musicrid){
    musicrid = musicrid.substring(6);
    return "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=${musicrid}&httpsStatus=1&reqId=f9204c10-1df1-11ec-8b4f-9f163660962a";
  }

}