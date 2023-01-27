// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'MLeaderBoardResult.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MLeaderBoardResult _$MLeaderBoardResultFromJson(Map<String, dynamic> json) {
  return MLeaderBoardResult(
    json['img'] as String,
    (json['musicList'] as List<dynamic>)
        .map((e) => MSong.fromJson(e as Map<String, dynamic>))
        .toList(),
  );
}

Map<String, dynamic> _$MLeaderBoardResultToJson(MLeaderBoardResult instance) =>
    <String, dynamic>{
      'img': instance.img,
      'musicList': instance.musicList,
    };
