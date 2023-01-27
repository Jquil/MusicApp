// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'MLeaderBoard.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MLeaderBoard _$MLeaderBoardFromJson(Map<String, dynamic> json) {
  return MLeaderBoard(
    json['name'] as String,
    (json['list'] as List<dynamic>)
        .map((e) => MLeaderBoardItem.fromJson(e as Map<String, dynamic>))
        .toList(),
  );
}

Map<String, dynamic> _$MLeaderBoardToJson(MLeaderBoard instance) =>
    <String, dynamic>{
      'name': instance.name,
      'list': instance.list,
    };
