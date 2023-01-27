import 'package:music/db/tb/TB_LeaderBoard.dart';
import 'package:json_annotation/json_annotation.dart';
part 'MLeaderBoardItem.g.dart';

@JsonSerializable()
class MLeaderBoardItem{

  @JsonKey(name: "sourceid")
  String sourceid;

  @JsonKey(name: "intro")
  String intro;

  @JsonKey(name: "name")
  String name;

  @JsonKey(name: "id")
  String id;

  @JsonKey(name: "source")
  String source;

  @JsonKey(name: "pic")
  String pic;

  @JsonKey(name: "pub")
  String pub;

  MLeaderBoardItem(this.sourceid, this.intro, this.name, this.id, this.source,
      this.pic, this.pub);

  factory MLeaderBoardItem.fromJson(Map<String,dynamic> json) => _$MLeaderBoardItemFromJson(json);
  Map<String,dynamic> toJson() => _$MLeaderBoardItemToJson(this);


  Map<String,dynamic> toMap(int typeId){
    return {
      TB_LeaderBoard.TL_NAME:name,
      TB_LeaderBoard.TL_INTRO:intro,
      TB_LeaderBoard.TL_PIC:pic,
      TB_LeaderBoard.TL_SOURCEID:sourceid,
      TB_LeaderBoard.TL_TYPE:typeId
    };
  }
}