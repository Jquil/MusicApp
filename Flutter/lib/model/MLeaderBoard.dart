import 'package:music/model/MLeaderBoardItem.dart';
import 'package:json_annotation/json_annotation.dart';
part 'MLeaderBoard.g.dart';

@JsonSerializable()
class MLeaderBoard{

  @JsonKey(name: "name")
  String name;

  @JsonKey(name: "list")
  List<MLeaderBoardItem> list;

  MLeaderBoard(this.name, this.list);

  factory MLeaderBoard.fromJson(Map<String,dynamic> json) => _$MLeaderBoardFromJson(json);
  Map<String,dynamic> toJson() => _$MLeaderBoardToJson(this);

  // flutter pub run build_runner build
}