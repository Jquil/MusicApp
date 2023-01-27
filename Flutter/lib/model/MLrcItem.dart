import 'package:json_annotation/json_annotation.dart';
part 'MLrcItem.g.dart';

@JsonSerializable()
class MLrcItem{

  @JsonKey(name: "lineLyric")
  String lineLyric;

  @JsonKey(name: "time")
  String time;

  MLrcItem(this.lineLyric, this.time);

  factory MLrcItem.fromJson(Map<String,dynamic> json) => _$MLrcItemFromJson(json);
  Map<String,dynamic> toJson() => _$MLrcItemToJson(this);
}