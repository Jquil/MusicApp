// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'MSinger.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MSinger _$MSingerFromJson(Map<String, dynamic> json) {
  return MSinger(
    json['pic'] as String,
    json['gener'] as String,
    json['name'] as String,
    json['id'] as int,
    json['country'] as String,
  );
}

Map<String, dynamic> _$MSingerToJson(MSinger instance) => <String, dynamic>{
      'pic': instance.pic,
      'gener': instance.gener,
      'name': instance.name,
      'id': instance.id,
      'country': instance.country,
    };
