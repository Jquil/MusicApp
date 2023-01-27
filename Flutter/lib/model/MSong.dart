
import 'package:music/db/tb/TB_Cache_Song.dart';
import 'package:music/db/tb/TB_SongSheet.dart';
import 'package:json_annotation/json_annotation.dart';
part 'MSong.g.dart';
@JsonSerializable()
class MSong{

   @JsonKey(name: "musicrid")
   String musicrid;

   // @JsonKey(name: "barrage")
   // String barrage;

   @JsonKey(name: "artist")
   String artist;

   // @JsonKey(name: "trend")
   // String trend;

   @JsonKey(name: "pic")
   String pic;

   // @JsonKey(name: "rid")
   // int rid;

   // @JsonKey(name: "hasmv")
   // int hasmv;

   @JsonKey(name: "album")
   String album;

   // 获取歌手下的歌曲时为String类型
   // @JsonKey(name: "albumid")
   // int albumid;

   // @JsonKey(name: "pay")
   // String pay;

   @JsonKey(name: "artistid")
   int artistid;

   @JsonKey(name: "albumpic")
   String albumpic;

   @JsonKey(name: "songTimeMinutes")
   String songTimeMinutes;

   @JsonKey(name: "pic120")
   String pic120;

   @JsonKey(name: "name")
   String name;


   MSong(this.musicrid, this.artist, this.pic, this.album, this.artistid,
      this.albumpic, this.songTimeMinutes, this.pic120, this.name);

  factory MSong.fromJson(Map<String,dynamic> json) => _$MSongFromJson(json);
   Map<String,dynamic> toJson() => _$MSongToJson(this);


   Map<String,dynamic> toMap(){
      return {
        TB_SongSheet.TSS_NAME:name,
        TB_SongSheet.TSS_PIC:pic,
        TB_SongSheet.TSS_MUSICRID:musicrid,
        TB_SongSheet.TSS_ARTIST:artist,
        TB_SongSheet.TSS_ARTISTID:artistid,
        TB_SongSheet.TSS_ALBUM:album,
        TB_SongSheet.TSS_ALBUMPIC:albumpic,
        TB_SongSheet.TSS_SONGTIMEMINUTES:songTimeMinutes,
        TB_SongSheet.TSS_HASMV:0
      };
   }

   Map<String,dynamic> toMap2(){
     return {
       TB_Cache_Song.TCS_NAME:name,
       TB_Cache_Song.TCS_PIC:pic,
       TB_Cache_Song.TCS_MUSICRID:musicrid,
       TB_Cache_Song.TCS_ARTIST:artist,
       TB_Cache_Song.TCS_ARTISTID:artistid,
       TB_Cache_Song.TCS_ALBUM:album,
       TB_Cache_Song.TCS_ALBUMPIC:albumpic,
       TB_Cache_Song.TCS_SONGTIMEMINUTES:songTimeMinutes,
       TB_Cache_Song.TCS_HASMV:0
     };
   }
}