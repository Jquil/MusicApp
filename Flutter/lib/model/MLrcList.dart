import 'package:music/model/MLrcItem.dart';
import 'package:json_annotation/json_annotation.dart';
part 'MLrcList.g.dart';

@JsonSerializable()
class MLrcList{

  @JsonKey(name: "lrclist")
  List<MLrcItem> lrclist;

  MLrcList(this.lrclist);

  factory MLrcList.fromJson(Map<String,dynamic> json) => _$MLrcListFromJson(json);
  Map<String,dynamic> toJson() => _$MLrcListToJson(this);
}