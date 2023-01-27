import 'package:music/model/MSong.dart';
import 'package:json_annotation/json_annotation.dart';
part 'MLeaderBoardResult.g.dart';

@JsonSerializable()
class MLeaderBoardResult{

  @JsonKey(name: "img")
  String img;

  @JsonKey(name: "musicList")
  List<MSong> musicList;

  MLeaderBoardResult(this.img, this.musicList);

  factory MLeaderBoardResult.fromJson(Map<String,dynamic> json) => _$MLeaderBoardResultFromJson(json);
  Map<String,dynamic> toJson() => _$MLeaderBoardResultToJson(this);
}