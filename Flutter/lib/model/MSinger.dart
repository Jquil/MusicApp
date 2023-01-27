import 'package:json_annotation/json_annotation.dart';
import 'package:music/db/tb/TB_Singer.dart';
part 'MSinger.g.dart';

@JsonSerializable()
class MSinger{

  @JsonKey(name: "pic")
  String pic;

  @JsonKey(name: "gener")
  String gener;

  @JsonKey(name: "name")
  String name;

  @JsonKey(name: "id")
  int id;

  @JsonKey(name: "country")
  String country;

  MSinger(this.pic, this.gener, this.name, this.id, this.country);


  factory MSinger.fromJson(Map<String,dynamic> json) => _$MSingerFromJson(json);
  Map<String,dynamic> toJson() => _$MSingerToJson(this);

  Map<String,dynamic> toMap() {
    return {
      TB_Singer.TS_PIC:pic,
      TB_Singer.TS_GENER:gener,
      TB_Singer.TS_NAME:name,
      TB_Singer.TS_ARTISTID:id,
      TB_Singer.TS_COUNTRY:country
    };
  }
}