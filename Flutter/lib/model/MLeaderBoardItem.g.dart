// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'MLeaderBoardItem.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MLeaderBoardItem _$MLeaderBoardItemFromJson(Map<String, dynamic> json) {
  return MLeaderBoardItem(
    json['sourceid'] as String,
    json['intro'] as String,
    json['name'] as String,
    json['id'] as String,
    json['source'] as String,
    json['pic'] as String,
    json['pub'] as String,
  );
}

Map<String, dynamic> _$MLeaderBoardItemToJson(MLeaderBoardItem instance) =>
    <String, dynamic>{
      'sourceid': instance.sourceid,
      'intro': instance.intro,
      'name': instance.name,
      'id': instance.id,
      'source': instance.source,
      'pic': instance.pic,
      'pub': instance.pub,
    };
